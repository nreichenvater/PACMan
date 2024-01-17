import React, { useEffect, useState, useRef } from 'react'
import Layout from '@/components/layouts'
import router from 'next/router'
import mongodb from '@/utils/mongodb'
import DeletePopUp from '@/components/popup/deletePopUp'
import _ from 'lodash'
import Notification from '@/service/notification'
import API from '@/api'
import fileUtils from '@/utils/fileUtils'
import jupyterService from '@/service/jupyterService'
import NotebookService from '@/service/notebookService'

export async function getServerSideProps(ctx) {
  //direct db fetch from server, which is not transferred to client (including db url)
  const notebooks = await mongodb.find('Notebook', {})
  if(notebooks && notebooks.length){
    return { props: { notebooks: JSON.parse(JSON.stringify(notebooks)) } }
  }
  return {
    props: {}
  }
}

const notebooks = (ctx) => {
  const [notebooks, setNotebooks] = useState([])
  const [deletePopUpVisible, setDeletePopUpVisible] = useState(false)
  const [notebookToDelete, setNotebookToDelete] = useState({})
  const [loading, setLoading] = useState(false)
  const [searchTerm, setSearchTerm] = useState("912da55c-c4ae-11ed-afa1-0242ac120002")
  const fileInputRef = useRef(null)
  const [chosenFileName, setChosenFileName] = useState("")
  const [fileContent, setFileContent] = useState("")

  const toggleDeletePopUp = () => setDeletePopUpVisible(vis => !vis)

  const deleteNotebook = async () => {
    if(notebookToDelete && !_.isEmpty(notebookToDelete)){
      setLoading(true)
      try {
        if(notebookToDelete._id){
          await API.delete(`/notebook/`+notebookToDelete._id)
        } else {
          await API.delete(`/notebook/`+notebookToDelete.id)
        }
        const nbs = [...notebooks]
        nbs.splice(notebooks.indexOf(notebookToDelete),1)
        setNotebooks(nbs)
        setLoading(false)
        Notification.success("The notebook was removed successfully")
      } catch(e) {
        setLoading(false)
        Notification.error(e)
      }
    } else  {
      Notification.error("Something went wrong :(")
    }
  }

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      if(searchTerm !== "912da55c-c4ae-11ed-afa1-0242ac120002"){ //prevent fetching initially
        fetchNotebooks()
      }
    }, 1000)
    return () => clearTimeout(delayDebounceFn)
  }, [searchTerm])

  useEffect(() => {
    if(ctx.notebooks){
      setNotebooks(ctx.notebooks)
    }
  }, [ctx])

  const fetchNotebooks = async (st) => {
    setLoading(true)
    try {
      const res = await API.get(`/notebook?searchTerm=`+searchTerm)
      if(res.status === 200){
        setNotebooks(res.data.notebooks)
        setLoading(false)
      }
    } catch(e) {
      setLoading(false)
      Notification.error(e)
    }
  }

  const fileChange = (e) => {
    const file = e.target.files[0]
    if (!file) {
      return
    }
    setChosenFileName(file.name)
    const reader = new FileReader()
    reader.onload = (e) => {
      let content
      try {
        content = JSON.parse(e.target.result)
      } catch(e){
        Notification.error("Please choose a valid JOSN-file")
        resetFile()
        return
      }
      content = jupyterService.moveTags(content)
      setFileContent(content)
    }
    reader.readAsText(file)
  }

  const saveNotebook = async (e) => {
    if(chosenFileName && fileContent){
      const url = "/notebook/upload"
      try {
        setLoading(true)
        const res = await API.post(url, fileContent)
        if(res.status === 200){
          setLoading(false)
          resetFile()
          Notification.success("The notebook was saved successfully")
          setSearchTerm("")
        }
      } catch(e) {
        setLoading(false)
        Notification.error(e)
      }
    } else {
      Notification.error("Please choose a valid JOSN-file")
    }
  }

  const resetFile = () => {
    setChosenFileName("")
    setFileContent("")
    fileInputRef.current.value = null
  }

  return (
    <Layout activeElement="notebooks" loading={loading}>
      <DeletePopUp visible={deletePopUpVisible} toggle={toggleDeletePopUp} delete={deleteNotebook} heading="Delete notebook" text={`Should the notebook ` + notebookToDelete.title + ` really be deleted?`}/>
      <div className="section">
        <div className="taskheaderrow">
          <div className="pageheading marginbottom">Notebooks</div>
          <div className="button newtask" onClick={() => router.push("/notebook")}>+ New</div>
        </div>
        <div className="inputrow choosefile">
          <div className="button choose" onClick={() => fileInputRef.current.click()}>Choose file</div>
          <input type="file" className="competenceinput" ref={fileInputRef} onChange={fileChange} />
          <input type="text" disabled={true} value={chosenFileName} className="input filename" placeHolder="No file chosen" />
          {chosenFileName && fileContent ? <div className="removefilebutton" onClick={resetFile}>-</div> : <></>}
          <div className="button" onClick={saveNotebook}>Upload</div>
        </div>
        <div className="searchwrapper">
          <input type="text" className="input fullwidth" placeholder="Title, task data, tutor, descriptions..."  onChange={(e) => {setSearchTerm(e.target.value)}}/>
        </div>
      </div>
      <div className="section">
        <div className="searchresultcontainer notebooks">
          {
            notebooks.length ?
            (
              <>
                {
                  notebooks.map(nb => (
                    <div className="searchresultbox" key={nb._id}>
                      <div className="searchresultinner notebook">
                        <div className="notebooktasktitle">{nb.title}</div>
                        <div className="cellbuttonpanel">
                          <img src="images/dump.png" className="editbutton" title="delete" onClick={() => {
                            setNotebookToDelete(old => nb)
                            toggleDeletePopUp()
                          }}/>
                          <img className="previewbutton" src="images/auge.png" title="preview" onClick={() => {
                            localStorage.setItem('previewJson',nb.json)
                            window.open("/preview", '_blank').focus()
                          }}/>
                          <img className="downloadbutton" src="images/download.png" title="download" onClick={() => NotebookService.downloadExistingNotebook(nb)} />
                        </div>
                      </div>
                    </div>
                  ))
                }
              </>
            ) : (
              <div className="nodatatext">No notebooks</div>
            )
          }
          
        </div>
      </div>
    </Layout>
  )
}

export default notebooks