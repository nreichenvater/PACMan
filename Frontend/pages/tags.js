import React, { useState, useEffect } from 'react'
import Layout from '@/components/layouts'
import mongodb from '@/utils/mongodb'
import DeletePopUp from '@/components/popup/deletePopUp'
import Notification from '@/service/notification'
import API from '@/api'

export async function getServerSideProps(ctx) {
  //direct db fetch from server, which is not transferred to client (including db url)
  const tags = await mongodb.find('Tag', {})
  if(tags && tags.length){
    return { props: { tags: JSON.parse(JSON.stringify(tags)) } }
  }
  return {
    props: {}
  }
}

const tags = (ctx) => {
  const [tags, setTags] = useState([])
  const [tagToDelete, setTagToDelete] = useState("")
  const [newTag, setNewTag] = useState("")
  const [deletePopUpVisible, setDeletePopUpVisible] = useState(false)
  const [loading, setLoading] = useState(false) 

  const toggleDeletePopUp = () => setDeletePopUpVisible(vis => !vis)

  useEffect(() => {
    if(ctx.tags){
      setTags(ctx.tags)
    }
  }, [ctx])

  const save = async () => {
    const body = {
      tag: newTag
    }
    try {
      setLoading(true)
      const res = await API.post(`/tag`,body)
      if(res.status === 200){
        Notification.success("The tag was saved sucessfully")
        fetch()
        setLoading(false)
        document.getElementById("newtaginput").value=""
      }
    } catch(e) {
      Notification.error(e)
    }
  }

  const fetch = async () => {
    //loading spinner
    try {
      setLoading(true)
      const res = await API.get(`/tag`)
      if(res.status === 200){
        setLoading(false)
        setTags(res.data.tags)
      }
    } catch(e) {
      setLoading(false)
      Notification.error(e)
    }
  }

  const deleteTag = async () => {
    try {
      setLoading(true)
      const res = await API.delete(`/tag/`+tagToDelete)
      if(res.status === 200){
        setLoading(false)
        Notification.success("The tag was removed successfully")
        fetch()
      }
    } catch(e) {
      setLoading(false)
      Notification.error(e)
    }
  }

  useEffect(() => {
    console.log(tags)
  }, [tags])

  return (
    <Layout activeElement="tags" loading={loading}>
      <DeletePopUp visible={deletePopUpVisible} toggle={toggleDeletePopUp} delete={deleteTag} heading="Tag löschen" text={`Soll der Tag ` + tagToDelete + ` wirklich gelöscht werden?`}/>
      <div className="section">
        <div className="taskheaderrow">
          <div className="pageheading marginbottom">Tags</div>
        </div>
      </div>
      <div className="section">
        <div className="sectionheading">Add tag</div>
        <div className="newtagwrapper">
          <input type="text" className="input" id="newtaginput" placeHolder="Easy" onChange={(e) => setNewTag(e.target.value)} />
          <div className="button" onClick={save}>Save</div>
        </div>
        
      </div>
      <div className="section">
        {
          tags.map(t => (
            <div className="tagwrapper">
              <div className="tagname">{t.tag}</div>
              <img src="images/dump.png" className="editbutton" onClick={() => {
                setTagToDelete(t.tag)
                toggleDeletePopUp()
              }}
              />
            </div>
          ))
        }
      </div>
    </Layout>
  )
}

export default tags