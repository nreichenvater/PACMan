import React, { useEffect, useState, useRef } from 'react'
import Layout from '@/components/layouts'
import { TagPicker } from 'rsuite'
import mongodb from '@/utils/mongodb'
import API from 'api'
import Notification from '@/service/notification'
import router from 'next/router'
import EditCellPopUp from '@/components/popup/editCellPopUp'
import TaskPreviewPopUp from '@/components/popup/taskPreviewPopUp'
import fileUtils from '@/utils/fileUtils'
import TaskExistsPopUp from '@/components/popup/taskExistsPopUp'

export async function getServerSideProps(ctx) {
  const tags = await mongodb.find('Tag')
  if(ctx.query && ctx.query.id){
    const id = ctx.query.id
    const tasks = await mongodb.find('Task',{'_id':id})
    if(tasks && tasks.length && tasks[0].file){
      const file = await mongodb.find('TaskFile',{'_id':tasks[0].file})
      if(file && file.length && file[0].fileName){
        tasks[0].fileName = file[0].fileName
      }
    }
    return { props: { task: JSON.parse(JSON.stringify(tasks[0])), isExistingTask: true, tags: JSON.parse(JSON.stringify(tags)) } }
  }
  return { props: { tags: JSON.parse(JSON.stringify(tags)) } }
}

const task = (ctx) => {
  const isExistingTask = ctx.isExistingTask || false
  const [tags,setTags] = useState([])
  const [task, setTask] = useState({
    title: "",
    tags: ctx.task ? ctx.task.tags : [],
    taskId: "",
    deletable: false,
    editable: false,
    language: "de",
    note: "",
    maxPoints: 4,
    additionalMetadata: {},
    cells: []
  })
  const [newCellData, setNewCellData] = useState({
    type: "markdown",
    source: "",
    metadata: {
      element_type: "",
      language: ""
    }
  })
  const [metaDataFormVisible, setMetaDataFormVisible] = useState(false)
  const [editCellPopUpVisible, setEditCellPopUpVisible] = useState(false)
  const toggleEditCellPopUp = () => setEditCellPopUpVisible(vis => !vis)
  const [cellIndexToEdit, setCellIndexToEdit] = useState(0)
  const metaDataKeyRef = useRef(null)
  const metaDataValRef = useRef(null)
  const [loading, setLoading] = useState(false)
  const [taskPreviewPopUpVisible, setTaskPreviewPopUpVisible] = useState(false)
  const [taskExistsPopUpVisible, setTaskExistsPopUpVisible] = useState(false)
  const [previewTask, setPreviewTask] = useState({json:""})

  const toggleTaskPreviewPopUp = () => setTaskPreviewPopUpVisible(vis => !vis)
  const toggleTaskExistsPopUp = () => setTaskExistsPopUpVisible(vis => !vis)

  const fileInputRef = useRef(null)
  const [fileType, setFileType] = useState("")
  const [chosenFileName, setChosenFileName] = useState("")
  const [fileContent, setFileContent] = useState("")

  useEffect(() => {
    console.log("ctx: ", ctx)
    if(ctx.task && ctx.task.metadata){
      const additionalMetadata = {}
      Object.entries(ctx.task.metadata).forEach(([k,v]) => {
        if(k != "task_id" && k != "deletable" && k != "editable" && k != "language" && k != "max_points"){
          additionalMetadata[k] = v
        }
      })
      const cells = []
      if(ctx.task.cells && ctx.task.cells.length){
        ctx.task.cells.forEach(c => {
          cells.push({
            type: String(c.type).toLowerCase(),
            source: c.source,
            metadata: c.metadata
          })
        })
      }
      setTask({
        ...task,
        title: ctx.task.title,
        tags: ctx.task.tags,
        note: ctx.task.note,
        taskId: ctx.task.metadata.task_id,
        deletable: ctx.task.metadata.deletable,
        editable: ctx.task.metadata.editable,
        language: ctx.task.metadata.language,
        maxPoints: ctx.task.metadata.max_points,
        additionalMetadata,
        cells,
        file: ctx.task.file
      })
      if(window != "undefined"){
        if(ctx.task.metadata.hasOwnProperty("deletable")){
          document.getElementById("deletable"+String(ctx.task.metadata.deletable)).checked = true
        }
        if(ctx.task.metadata.hasOwnProperty("editable")){
          document.getElementById("editable"+String(ctx.task.metadata.editable)).checked = true
        }
      }
        setChosenFileName(ctx.task.fileName)
        setFileType(ctx.task.fileType)
        setFileContent("placeholder")
    }
    if(ctx.tags){
      console.log(ctx.tags)
      setTags(ctx.tags.map(item => ({ label: item.tag, value: item.tag })))
    }
  }, [ctx])

  const handleTaskChange = (e) => {
    if(e){
      const {id,value} = e.target
      if(String(id).includes("editable") || String(id).includes("deletable")){
        setTask({
          ...task,
          [String(id).includes("editable") ? "editable" : String(id).includes("deletable") ? "deletable" : id]: value === "yes" ? true : value === "no" ? false : value
        })
      } else {
        setTask({
          ...task,
          [id]: value
        })
      }
    }
  }

  const handleNewCellChange = (e) => {
    if(e){
      const {id,value} = e.target
      setNewCellData({
        ...newCellData,
        [id]: value
      })
    }
  }

  const addCell = () => {
    if(newCellData.type && newCellData.source){
      const cells = task.cells
      cells.push({
        type: newCellData.type,
        source: newCellData.source,
        metadata: {
          element_type: newCellData.metadata.element_type,
          language: newCellData.metadata.language,
          task_id: task.taskId
        }
      })
      setTask({...task,cells})
      setNewCellData({...newCellData,source:"", metadata: { element_type: "" }})
      document.getElementById("source").value = ""
      document.getElementById("elementtype").value = ""
    } else {
      Notification.error("Please enter a source text")
    }
  }

  const handleTagsChanged = (tags) => {
    setTask({
      ...task,
      tags
    })
  }

  const checkTaskExists = async () => {
    const err = taskService.validateTaskInput(task)
    if(err){
      Notification.error(err)
      return
    }

    if(isExistingTask){ //edit mode
      save()
    } else {
      try {
        const res = await API.get(`task/`+task.taskId+`/`+task.language)
        console.log(res)
        if(res.status === 200 && !res.data.taskExists){
          save()
        } else if(res.status === 200 && res.data.taskExists){
          toggleTaskExistsPopUp()
        }
      } catch(e) {
        Notification.error(e)
      }
    }
  }

  const save = async () => {
    setLoading(true)
    const body = {
      title: task.title,
      cells: task.cells,
      tags: task.tags,
      metadata: {
        ...task.additionalMetadata,
        task_id: task.taskId,
        deletable: task.deletable,
        editable: task.editable,
        language: task.language,
        max_points: task.maxPoints
      },
      note: task.note,
      file : {
        fileType: fileType,
        fileName: chosenFileName,
        stringContent: fileUtils.toBase64(fileContent)
      }
    }
    try {
      const res = await API.post(`/task`, body)
      if(res.status === 200){
        setLoading(false)
        Notification.success("The task was successfully saved")
      }
    } catch (e) {
      setLoading(false)
      Notification.error(e)
    }
  }

  const addMetaData = () => {
    if(metaDataKeyRef.current && metaDataValRef.current && metaDataKeyRef.current.value && metaDataValRef.current.value){
      setTask({
        ...task,
        additionalMetadata: {
          ...task.additionalMetadata,
          [metaDataKeyRef.current.value]: metaDataValRef.current.value
        }
      })
      metaDataKeyRef.current.value = ""
      metaDataValRef.current.value = ""
      setMetaDataFormVisible(false)
    } else {
      Notification.error("Please enter a valid key-value-pair")
    }
  }

  const preparePreviewTask = () => {
    const temp = { cells: [] }
    temp.cells.push({
      cell_type: "markdown",
      source: "# " + task.title 
    })
    task.cells.forEach(c => {
      temp.cells.push({
        cell_type: c.type,
        source: c.source
      })
    })
    const t = {}
    t.json = JSON.stringify(temp)
    setPreviewTask(t)
    toggleTaskPreviewPopUp()
  }

  const fileChange = (e) => {
    const file = e.target.files[0]
    if (!file) { return }
    setChosenFileName(file.name)
    const reader = new FileReader()
    reader.onload = (e) => {
      setFileContent(e.target.result)
    }
    reader.readAsArrayBuffer(file)
  }

  const resetFile = () => {
    setChosenFileName("")
    setFileContent("")
    fileInputRef.current.value = null
  }

  const downloadFile = async () => {
    if(!task.file && !chosenFileName) return
    if(task.file && chosenFileName){
      try {
        const res = await API.get(`/file/`+task.file)
        fileUtils.save(chosenFileName, fileType, new Uint8Array(res.data.binContent.data))
      } catch(e) {console.log(e)}
    } else if(chosenFileName && fileType && fileContent) {
      fileUtils.save(chosenFileName, fileType, fileContent)
    }
    
  }

  return (
    <Layout activeElement="tasks" loading={loading}>
      {editCellPopUpVisible ? <EditCellPopUp toggle={toggleEditCellPopUp} updateTask={(t) => setTask(t)} task={task} cellIndexToEdit={cellIndexToEdit} /> : <></>}
      {taskPreviewPopUpVisible ? <TaskPreviewPopUp toggle={toggleTaskPreviewPopUp} task={previewTask}/> : <></>}
      {taskExistsPopUpVisible ? <TaskExistsPopUp toggle={toggleTaskExistsPopUp} save={save} visible={taskExistsPopUpVisible} /> : <></>}
      <div className="taskwrapper">
        <div className="section taskheader">
          <img src="images/left-arrow.png" className="backarrowbutton" onClick={() => router.push("/tasks")}/>
          <div className="pageheading">{isExistingTask ? "Edit task" : "New task"}</div>
          <div className="flex" />
          <div className="taskheaderbuttongroup">
            <div className="button outline" onClick={preparePreviewTask}>Preview</div>
            <div className="button accent" onClick={checkTaskExists}>Save</div>
          </div>
        </div>
        <div className="section">
          <div className="titletagsrow">
            <div className="inputsection tasktitle">
              <div className="inputheading">Title</div>
              <input type="text" id="title" className="input" placeHolder="Task title" value={task.title} onChange={handleTaskChange} />
            </div>
            <div>
              <div className="inputheading">Tags</div>
              <TagPicker data={tags} style={{ width: 300 }} onChange={handleTagsChanged} defaultValue={task.tags} />
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
              <div className="inputheading">Task-ID</div>
              <input type="text" id="taskId" className="input" placeHolder="Unique Task-ID" value={task.taskId} onChange={handleTaskChange} />
            </div>
            <div className="inputsection">
              <div className="inputheading">Default max-points</div>
              <input type="text" id="maxPoints" className="input" defaultValue={4} value={task.maxPoints} onChange={handleTaskChange} />
            </div>
            <div className="radiosection">
              <div className="inputheading">deletable</div>
              <div style={{display:"flex",gap:"5px"}} onChange={handleTaskChange}>
                <input type="radio" id="deletablefalse" name="deletableradio" value="no" defaultChecked />
                <label for="deletableno">no</label>
                <input type="radio" id="deletabletrue" name="deletableradio" value="yes" />
                <label for="deletableyes">yes</label>
              </div>
            </div>
            <div className="radiosection">
              <div className="inputheading">editable</div>
              <div style={{display:"flex",gap:"5px"}} onChange={handleTaskChange}>
                <input type="radio" id="editablefalse" name="editableradio" value="no" defaultChecked />
                <label for="editableno">no</label>
                <input type="radio" id="editabletrue" name="editableradio" value="yes" />
                <label for="editableyes">yes</label>
              </div>
            </div>
            <div className="selectsection">
              <div className="inputheading">language</div>
              <select id="language" onChange={handleTaskChange} value={task.language}>
                <option value="de">DE</option>
                <option value="en">EN</option>
              </select>
            </div>
          </div>
          <div className="metadatawrapper">
            {
              Object.entries(task.additionalMetadata).map(([k,v]) => (
                <>
                  {
                    k != "task_id" && k != "max_points" && k != "language" ?
                    (
                      <div className="metadataitem">
                        <div className="metadatakey">{k+`:`}</div>
                        <div className="metadataval">{v}</div>
                        <div className="removebutton" onClick={() => {
                          const amd = task.additionalMetadata
                          delete amd[k]
                          setTask({...task,additionalMetadata: amd})
                        }}>-</div>
                      </div>
                    ) : <></>
                  }
                </>
              ))
            }
          </div>
          <div className="inputsection">
            <div className="inputheading">external file</div>
            <div className="inputsection fileupload">
              <div className="button" onClick={() => fileInputRef.current.click()}>Choose File</div>
              <input type="file" className="competenceinput" ref={fileInputRef} onChange={fileChange} />
              <input type="text" disabled={true} value={chosenFileName} className="input filename wide" placeHolder="No file chosen" />
              {chosenFileName && fileContent ? <div className="removefilebutton" onClick={resetFile}>-</div> : <></>}
            </div>
          </div>
          <div className="inputsection fileupload download">
            <div className={chosenFileName && fileContent ? "button" : "button disabled"} onClick={downloadFile}>Download File</div>
          </div>
          
          <div className="inputsection">
            <div className="inputheading">Internal note</div>
            <input type="text" id="note" className="input" value={task.note} onChange={handleTaskChange} />
          </div>
        </div>
        <div className="section">
          <div className="newcellinputwrapper">
            <div className="newcellheading">New cell</div>
            <div className="newcelladdbutton" onClick={addCell}>Add</div>
            <div className="cellinputrow">
              <div className="selectsection smallgap">
                <div className="inputheading">Type</div>
                <select className="tasktypeselect" id="type" onChange={handleNewCellChange}>
                  <option value="markdown">markdown</option>
                  <option value="code">code</option>
                  <option value="raw">raw</option>
                </select>
              </div>
              <div className="inputsection">
                <div className="inputheading">element_type</div>
                <select id="elementtype" className="input elementtype" placeholder="" onChange={(e) => setNewCellData({...newCellData, metadata: { ...newCellData.metadata, element_type: e.target.value}})}>
                  <option value=""></option>
                  <option value="student_data">student_data</option>
                  <option value="task_description">task_description</option>
                  <option value="task_student_solution">task_student_solution</option>
                  <option value="task_sample_solution">task_sample_solution</option>
                  <option value="task_example">task_example</option>
                </select>
              </div>
              <div className="inputsection">
                <div className="inputheading">language</div>
                <input id="language" className="input elementtype" placeHolder="en"
                  onChange={(e) => setNewCellData({...newCellData, metadata: { ...newCellData.metadata, language: e.target.value}})}
                />
              </div>
            </div>
            <div className="inputsection">
              <div className="inputheading">Source</div>
              <textarea id="source" className="input cellsource" placeHolder="Source text" rows="5" onChange={handleNewCellChange} />
            </div>
          </div>
        </div>
        <div className="horizontalline" />
        <div className="section">
          <div className="sectionheading" style={{marginBottom:"15px"}}>Cells</div>
          <div className="taskcellcontainer">
            {
              task.cells && task.cells.length ?
              (
                task.cells.map(c => (
                  <div className="taskcellwrapper">
                    <div className="cellbuttonpanel">
                      {task.cells.indexOf(c) > 0 ? <div className="cellbutton" onClick={() => setTask(taskService.moveElem(c,1))}>▲</div> : <></>}
                      {task.cells.indexOf(c) < task.cells.length-1 ? <div className="cellbutton"onClick={() =>  setTask(taskService.moveElem(c,2))}>▼</div> : <></>}
                      <img src="images/bleistift.png" className="editbutton"
                        onClick={() => {
                          setCellIndexToEdit(task.cells.indexOf(c))
                          toggleEditCellPopUp()
                        }}/>
                      <img src="images/dump.png" className="editbutton" onClick={() => {
                        const cells = task.cells
                        cells.splice(cells.indexOf(c),1)
                        setTask({...task,cells})
                      }} />
                    </div>
                    <div className="cellrow">
                      <div className="cellcol">
                        <div className="inputheading dark">Type</div>
                        <div className="taskcellsource">{c.type}</div>
                      </div>
                      <div className="cellcol">
                        <div className="inputheading dark">element_type</div>
                        <div className="taskcellsource">{c.metadata && c.metadata.element_type ? c.metadata.element_type : ""}</div>
                      </div>
                      <div className="cellcol">
                        <div className="inputheading dark">language</div>
                        <div className="taskcellsource">{c.metadata && c.metadata.language ? c.metadata.language : ""}</div>
                      </div>
                    </div>
                    <div className="inputheading dark margintop">Source</div>
                    <div className="taskcellsource">{c.source}</div>
                  </div>
                ))
              ) : (
                <div>No cells</div>
              )
            }
          </div>
        </div>
      </div>
    </Layout>
  )
}

export default task