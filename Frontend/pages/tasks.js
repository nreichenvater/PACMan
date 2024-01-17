import React, { useEffect, useState } from 'react'
import Layout from '@/components/layouts'
import router from 'next/router'
import mongodb from '@/utils/mongodb'
import DeletePopUp from '@/components/popup/deletePopUp'
import _ from 'lodash'
import Notification from '@/service/notification'
import API from '@/api'
import TaskPreviewPopUp from '@/components/popup/taskPreviewPopUp'

export async function getServerSideProps(ctx) {
  //direct db fetch from server, which is not transferred to client (including db url)
  const tasks = await mongodb.find('Task', {})
  if(tasks && tasks.length){
    return { props: { tasks: JSON.parse(JSON.stringify(tasks)) } }
  }
  return {
    props: {}
  }
}

const tasks = (ctx) => {
  const [tasks, setTasks] = useState([])
  const [deletePopUpVisible, setDeletePopUpVisible] = useState(false)
  const [taskPreviewPopUpVisible, setTaskPreviewPopUpVisible] = useState(false)
  const [taskToDelete, setTaskToDelete] = useState({})
  const [loading, setLoading] = useState(false)
  const [searchTerm, setSearchTerm] = useState("912da55c-c4ae-11ed-afa1-0242ac120002")
  const [previewTask, setPreviewTask] = useState({json:""})

  const toggleDeletePopUp = () => setDeletePopUpVisible(vis => !vis)
  const toggleTaskPreviewPopUp = () => setTaskPreviewPopUpVisible(vis => !vis)

  useEffect(() => {
    if(ctx.tasks){
      setTasks(ctx.tasks)
    }
  }, [ctx])

  useEffect(() => {
    console.log(tasks)
  }, [tasks])

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      if(searchTerm !== "912da55c-c4ae-11ed-afa1-0242ac120002"){ //prevent fetching initially
        fetchTasks()
      }
    }, 1000)
    return () => clearTimeout(delayDebounceFn)
  }, [searchTerm])

  const fetchTasks = async () => {
    setLoading(true)
    try {
      const res = await API.get(`/tasks?searchTerm=`+searchTerm)
      if(res.status === 200){
        setTasks(res.data.tasks)
        setLoading(false)
      }
    } catch(e) {
      setLoading(false)
      Notification.error(e)
    }
  }
 
  const deleteTask = async () => {
    if(taskToDelete && !_.isEmpty(taskToDelete)){
      setLoading(true)
      try {
        if(taskToDelete._id){
          await API.delete(`/task/`+taskToDelete._id)
        } else {
          await API.delete(`/task/`+taskToDelete.id)
        }
        const ts = [...tasks]
        ts.splice(tasks.indexOf(taskToDelete),1)
        setTasks(ts)
        setLoading(false)
        Notification.success("The task was successfully removed")
      } catch(e) {
        setLoading(false)
        Notification.error(e)
      }
    } else  {
      Notification.error("Something went wrong :(")
    }
  }

  return (
    <Layout activeElement="tasks" loading={loading}>
      <DeletePopUp visible={deletePopUpVisible} toggle={toggleDeletePopUp} delete={deleteTask} heading="Remove task" text={`Should the task ` + taskToDelete.title + ` really be removed?`}/>
      {taskPreviewPopUpVisible ? <TaskPreviewPopUp toggle={toggleTaskPreviewPopUp} task={previewTask}/> : <></>}
      <div className="section">
        <div className="taskheaderrow">
          <div className="pageheading marginbottom">Tasks</div>
          <div className="button newtask" onClick={() => router.push("/task")}>+ New</div>
        </div>
        <div className="searchwrapper">
          <input type="text" className="input fullwidth" placeholder="Title, task-ID, description..." onChange={(e) => {setSearchTerm(e.target.value)}}/>
        </div>
      </div>
      <div className="section">
        <div className="searchresultcontainer tasks">
          {
            tasks.length ?
            (
              <>
                {
                  tasks.map(t => (
                    <div className="searchresultbox" key={t._id}>
                      <div className="searchresultinner">
                      <div className="notebooktasktitle">{t.metadata.language === "de" ? t.title + ` [` + t.metadata.max_points + ` Punkte]` : t.title + ` [` + t.metadata.max_points + ` Points]`}</div>
                      <div style={{display:"flex", marginTop:"5px", marginBottom:"5px"}}>
                        {
                          t.tags ?
                          (
                            <>
                              {
                                t.tags.map(tag => (
                                  <div className="tagtext" style={{marginRight: "5px"}}>{`#`+tag}</div>
                                ))
                              }
                            </>
                          ) : (
                            <></>
                          )
                        }
                      </div>
                      <div className="inputheading dark margintop">Task-ID</div>
                      <div className="notebooktaskid">{t.metadata.task_id}</div>
                      {(t.comp_prim && t.comp_prim.length) || (t.comp_sec && t.comp_sec.length) ? <div className="inputheading dark margintop">Competences</div> : <></>}
                      {
                        t.comp_prim ? 
                        (
                          t.comp_prim.map(c => (
                            <div className="treetask nomargin">
                              <div className="comptype prim">PRIMARY</div>
                              <div className="weightedtaskinfo bold">{`CompetenceID: `}</div>
                              <div className="weightedtaskinfo">{c.comp_id}</div>
                              <div className="weightedtaskinfo bold">{`Weight: `}</div>
                              <div className="weightedtaskinfo">{c.weight}</div>
                            </div>
                          ))
                        ) : <></>
                      }
                      {
                        t.comp_sec ? 
                        (
                          t.comp_sec.map(c => (
                            <div className="treetask nomargin">
                              <div className="comptype sec">SECONDARY</div>
                              <div className="weightedtaskinfo bold">{`CompetenceID: `}</div>
                              <div className="weightedtaskinfo">{c.comp_id}</div>
                              <div className="weightedtaskinfo bold">{`Weight: `}</div>
                              <div className="weightedtaskinfo">{c.weight}</div>
                            </div>
                          ))
                        ) : <></>
                      }
                      <div className="cellbuttonpanel">
                        <img src="images/bleistift.png" className="editbutton" onClick={() => {
                          if(t._id){
                            router.push("/task?id="+t._id)
                          } else { router.push("/task?id="+t.id) }
                        }} />
                        <img src="images/dump.png" className="editbutton" onClick={() => {
                          setTaskToDelete(old => t)
                          toggleDeletePopUp()
                        }}/>
                        <img className="previewbutton" src="images/auge.png" onClick={() => {
                          setPreviewTask(t)
                          setTaskPreviewPopUpVisible(true)
                        }}/>
                      </div>
                      <div className="createdpanel">
                        <div className="tagtext">{`created: `+new Date(t.createdAt).toDateString()}</div>
                        {Math.abs(new Date(t.createdAt).getTime() - new Date(t.updatedAt).getTime()) / 1000 > 2 ? <div className="tagtext">{`updated: `+new Date(t.updatedAt).toDateString()}</div> : <></>}
                      </div>
                    </div>
                    </div>
                  ))
                }
              </>
            ) : (
              <div className="nodatatext">No tasks</div>
            )
          }
        </div>
      </div>
    </Layout>
  )
}

export default tasks