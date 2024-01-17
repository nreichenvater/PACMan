import React, { useEffect, useState, useRef } from 'react'
import Layout from '@/components/layouts'
import arrayUtils from '@/utils/arrayUtils'
import PopUp from '@/components/popup/addTaskPopUp'
import router from 'next/router'
import Notification from '@/service/notification'
import API from '@/api'
import fileUtils from '@/utils/fileUtils'
import tasks from './notebooks'
import NotebookService from '@/service/notebookService'

const notebook = (ctx) => {
  const [metaDataFormVisible, setMetaDataFormVisible] = useState(false)
  const metaDataKeyRef = useRef(null)
  const metaDataValRef = useRef(null)
  const [taskPopUpVisible, setTaskPopUpVisible] = useState(false)
  const [includeGradingTable, setIncludeGradingTable] = useState(true)
  const [notebook, setNotebook] = useState({
    title: "",
    metadata: {
      tutor: "",
      assignment_id: "",
      submissiondate: "",
      deletable: false,
      editable: false,
      language: "DE"
    },
    tasks: [],
    info: `**Submission until:**\n\n**Name:**\n\n**Student ID:**\n\n**Team members:**\n\n**Effort in hours (including time in class):**`
  })
  const [additionalMetadata, setAdditionalMetadata] = useState({})
  const [addedTaskIds, setAddedTaskIds] = useState([])
  const [loading, setLoading] = useState(false)

  const toggleTaskPopup = () => setTaskPopUpVisible(vis => !vis)

  const handleChange = (e) => {
    if(e){
      const {id,value} = e.target
      setNotebook({...notebook,[id]:value})
    }
  }

  const addMetaData = () => {
    if(metaDataKeyRef.current && metaDataValRef.current && metaDataKeyRef.current.value && metaDataValRef.current.value){
      setAdditionalMetadata({
        ...additionalMetadata,
        [metaDataKeyRef.current.value]: metaDataValRef.current.value
      })
      metaDataKeyRef.current.value = ""
      metaDataValRef.current.value = ""
      setMetaDataFormVisible(false)
    } else {
      Notification.error("Please enter key and value")
    }
  }

  const moveElem = (elem, mode) => {
    const index = notebook.tasks.indexOf(elem)
    const tasks = notebook.tasks
    if(mode === 1){
      arrayUtils.swap(tasks,index,index-1)
    } else if(mode === 2){
      arrayUtils.swap(tasks,index,index+1)
    }
    setNotebook({...notebook,tasks})
  }

  const save = async () => {
    const err = validateNotebookInput()
    if(err){
      Notification.error(err)
      return
    }
    setLoading(true)
    try {
      const metadata = {
        ...notebook.metadata,
        ...additionalMetadata
      }
      const body = { 
        title: notebook.title,
        metadata,
        tasks: notebook.tasks,
        info: notebook.info,
        includeGradingTable
      }
      const res = await API.post(`/notebook`, body)
      if(res.status === 200){
        NotebookService.downloadPackage(notebook, res.data.title, res.data.json)
        setLoading(false)
        Notification.success("The notebook was saved successfully")
      }
    } catch(e) {
      setLoading(false)
      Notification.error(e)
    }
  }

  const validateNotebookInput = () => {
    if(!notebook.title){
      return "Please enter a title for the notebook"
    }
    if(!notebook.tasks.length){
      return "Please add tasks to the notebook"
    }
    if(!notebook.metadata.assignment_id){
      return "Please enter an assignment_id"
    }
    if(!notebook.metadata.tutor){
      return "Please enter a tutor for the notebook"
    }
  }

  const addTask = (task) => {
    const tasks = [...notebook.tasks]
    tasks.push(task)
    setNotebook({...notebook,tasks})
    const addedIs = [...addedTaskIds]
    addedIs.push(task.id)
    setAddedTaskIds(addedIs)
  }

  const removeTask = (task) => {
    const tasks = [...notebook.tasks]
    tasks.splice(tasks.indexOf(task),1)
    setNotebook({...notebook,tasks})
    const addedIs = [...addedTaskIds]
    addedIs.splice(addedIs.indexOf(task.id),1)
    setAddedTaskIds(addedIs)
  }

  const handleMetadataChange = (e) => {
    if(e){
      const {id,value} = e.target
      const md = {...notebook.metadata,[String(id).includes("editable") ? "editable" : String(id).includes("deletable") ? "deletable" : id]: value === "yes" ? true : value === "no" ? false : value}
      setNotebook({...notebook,metadata:md})
    }
  }

  const previewNotebook = async () => {
    setLoading(true)
    try {
      const metadata = {
        ...notebook.metadata,
        ...additionalMetadata
      }
      const body = { 
        title: notebook.title,
        metadata,
        tasks: notebook.tasks,
        info: notebook.info,
        includeGradingTable
      }
      const res = await API.post(`/notebook/preview`, body)
      if(res.status === 200){
        localStorage.setItem('previewJson',res.data.json)
        setLoading(false)
        window.open("/preview", '_blank').focus()
      }
    } catch(e) {
      setLoading(false)
      Notification.error(e)
    }
  }

  const handleGradingTableChange = (e) => {
    if(e){
      const val = e.target.value
      if(val === "y"){
        setIncludeGradingTable(true)
      } else {
        setIncludeGradingTable(false)
      }
    }
  }

  return (
    <Layout activeElement="notebooks" loading={loading}>
      {taskPopUpVisible ? <PopUp toggle={toggleTaskPopup} addTask={addTask} removeTask={removeTask} addedIds={addedTaskIds} /> : <></>}
      <div className="notebookwrapper">
        <div className="section taskheader">
          <img src="images/left-arrow.png" className="backarrowbutton" onClick={() => router.push("/notebooks")}/>
          <div className="pageheading">New Notebook</div>
          <div className="flex" />
          <div className="taskheaderbuttongroup">
            <div className="button outline" onClick={previewNotebook}>Preview</div>
            <div className="button accent" onClick={save}>Save</div>
          </div>
        </div>
        <div className="section">
          <div className="titletagsrow">
            <div className="inputsection tasktitle">
              <div className="inputheading">Title</div>
              <input type="text" id="title" className="input" placeHolder="Title of the notebook" value={notebook.title} onChange={handleChange} />
            </div>
            <div>
              <div className="inputheading">Tutor</div>
              <input type="text" className="input tutor" placeHolder="Tutor"
                onChange={(e) => {
                  const md = notebook.metadata
                  md.tutor = e.target.value
                  setNotebook({...notebook,metadata:md})
              }}/>
            </div>
          </div>
          <div style={{display:"flex",gap:"10px",alignItems:"center",marginBottom:"15px"}}>
            <div className="sectionheading">Metadata</div>
            <div className="addbutton" onClick={() => setMetaDataFormVisible(true)}>+</div>
          </div>
          <div className="metadataaddform" style={metaDataFormVisible? {display:"flex",gap:"10px",alignItems:"center",marginBottom:"15px"} : {display:"none"}}>
            <input type="text" className="input metadata" placeHolder="key" ref={metaDataKeyRef} />
            <input type="text" className="input metadata" placeHolder="value" ref={metaDataValRef} />
            <div className="addmetadatabutton" onClick={addMetaData}>+</div>
          </div>

          <div className="metadatamainrow">
            <div className="inputsection taskid">
              <div className="inputheading">assignment_id</div>
              <input type="text" id="assignment_id" className="input" placeHolder="Assignment-ID" value={notebook.metadata.assignment_id} onChange={handleMetadataChange} />
            </div>
            <div className="inputsection">
              <div className="inputheading">submissiondate</div>
              <input type="text" id="submissiondate" className="input" placeHolder="01.05.2023" value={notebook.metadata.submissiondate} onChange={handleMetadataChange} />
            </div>
            <div className="radiosection">
              <div className="inputheading">deletable</div>
              <div style={{display:"flex",gap:"5px"}} onChange={handleMetadataChange}>
                <input type="radio" id="deletablefalse" name="deletableradio" value="no" defaultChecked />
                <label for="deletableno">no</label>
                <input type="radio" id="deletabletrue" name="deletableradio" value="yes" />
                <label for="deletableyes">yes</label>
              </div>
            </div>
            <div className="radiosection">
              <div className="inputheading">editable</div>
              <div style={{display:"flex",gap:"5px"}} onChange={handleMetadataChange}>
                <input type="radio" id="editablefalse" name="editableradio" value="no" defaultChecked />
                <label for="editableno">no</label>
                <input type="radio" id="editabletrue" name="editableradio" value="yes" />
                <label for="editableyes">yes</label>
              </div>
            </div>
            <div className="selectsection">
              <div className="inputheading">language</div>
              <select id="language" onChange={handleMetadataChange} value={notebook.metadata.language}>
                <option value="de">DE</option>
                <option value="en">EN</option>
              </select>
            </div>
          </div>

          <div className="radiosection smallgap">
            <div className="inputheading">include grading table</div>
            <div style={{display:"flex",gap:"5px"}} onChange={handleGradingTableChange}>
              <input type="radio" id="gradingtrue" name="gradingyesno" value="y" defaultChecked />
              <label for="gradingtrue">yes</label>
              <input type="radio" id="gradingfalse" name="gradingyesno" value="n" />
              <label for="gradingfalse">no</label>
            </div>
          </div>

          <div className="metadatawrapper">
            {
              Object.entries(additionalMetadata).map(([k,v]) => (
                  k !== "tutor" ?
                  (
                    <div className="metadataitem">
                      <div className="metadatakey">{k+`:`}</div>
                      <div className="metadataval">{v}</div>
                      <div className="removebutton" onClick={() => {
                        const md = additionalMetadata
                        delete md[k]
                        setAdditionalMetadata(additionalMetadata)
                      }}>-</div>
                    </div>
                  ) : (
                    <></>
                  )
              ))
            }
          </div>

          <div className="inputsection">
            <div className="inputheading">info</div>
            <textarea id="info" className="input cellsource"
              defaultValue={`**Submission until:**\n\n**Name:**\n\n**Student ID:**\n\n**Team members:**\n\n**Effort in hours (including time in class):**`}
              rows="10" onChange={handleChange} 
            />
          </div>

        </div>
        <div className="section">
          <div style={{display:"flex",gap:"10px",alignItems:"center",marginBottom:"15px"}}>
            <div className="sectionheading">Tasks</div>
            <div className="addbutton" onClick={toggleTaskPopup}>+</div>
          </div>
          <div className="taskcontainer">
            {
              notebook.tasks && notebook.tasks.length ?
              (
                notebook.tasks.map(c => (
                  <div className="notebooktaskwrapper">
                    <div className="inputheading dark">Title</div>
                    <div className="notebooktasktitle">{c.title}</div>
                    <div className="inputheading dark margintop">Points</div>
                    <input type="text" defaultValue={c.metadata.max_points || ""} className="input maxpoints"
                      onChange={(e) => {
                        const tasks = [...notebook.tasks]
                        tasks[tasks.indexOf(c)].metadata.max_points = e.target.value
                        setNotebook({...notebook,tasks})
                    }}/>
                    <div className="inputheading dark margintop">Task-ID</div>
                    <div className="notebooktaskid">{c.metadata.task_id}</div>
                    <div className="cellbuttonpanel">
                      {notebook.tasks.indexOf(c) > 0 ? <div className="cellbutton" onClick={() => moveElem(c,1)}>▲</div> : <></>}
                      {notebook.tasks.indexOf(c) < notebook.tasks.length-1 ? <div className="cellbutton"onClick={() => moveElem(c,2)}>▼</div> : <></>}
                      <img src="images/bleistift.png" className="editbutton" onClick={() => router.push("/task?id="+c.id)} />
                      <img src="images/dump.png" className="editbutton" onClick={() => {
                        const tasks = [...notebook.tasks]
                        tasks.splice(tasks.indexOf(c),1)
                        setNotebook({...notebook,tasks})
                      }} />
                    </div>
                  </div>
                ))
              ) : (
                <></>
              )
            }
          </div>
        </div>

      </div>
    </Layout>
  )
}

export default notebook