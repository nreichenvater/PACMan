import React, { useEffect, useState, useId } from 'react'
import API from '@/api'
import Notification from '@/service/notification'
import router from 'next/router'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import rehypeRaw from 'rehype-raw'
import TreeView from '@mui/lab/TreeView'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'
import ChevronRightIcon from '@mui/icons-material/ChevronRight'
import TreeItem from '@mui/lab/TreeItem'

const popup = (ctx) => {
  const [taskSearchTerm, setTaskSearchTerm] = useState("")
  const [compSearchTerm, setCompSearchTerm] = useState("")
  const [tasks, setTasks] = useState([])
  const [competences, setCompetences] = useState([])
  const [language, setLanguage] = useState("")
  const [visibleSection, setVisibleSection] = useState("tasks")
  const [expandedNodes, setExpandedNodes] = useState([])
  const [previewTask, setPreviewTask] = useState({})

  useEffect(() => {
    document.body.style.overflow = 'hidden'
    return () => document.body.style.overflow = 'unset'
  }, [])

  useEffect(() => {
    console.log(expandedNodes)
  }, [expandedNodes])

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      findTasks()
    }, 1000)
    return () => clearTimeout(delayDebounceFn)
  }, [taskSearchTerm])

  useEffect(() => {
    findTasks()
  }, [language])

  const findTasks = async () => {
    let url = `/tasks?searchTerm=`+taskSearchTerm
    if(language !== "all"){
      url = url + "&language=" + language
    }
    try {
      const tasks = await API.get(url).then(res => res.data.tasks)
      setTasks(tasks)
    } catch(e) {
      Notification.error(e)
    }
  }

  const findCompetences = async () => {
    let url = `/competence/tree?searchTerm=`+compSearchTerm
    try {
      const comps = await API.get(url).then(res => res.data.nodes)
      console.log("comps: ", comps)
      setCompetences(comps)
    } catch(e) {
      Notification.error(e)
    }
  }

  useEffect(() => {
    findCompetences()
  }, [compSearchTerm])

  const toggleVisibleSection = () => {
    if(visibleSection === "tasks"){
      setVisibleSection("competences")
      setTasks([])
      findCompetences()
    } else {
      setVisibleSection("tasks")
      findTasks()
    }
  }

  const prepairPreview = (t) => {
    const task = JSON.parse(t.json)
    if(task.cells){
      task.cells.forEach(c => {
        if(Array.isArray(c.source)){
          let source = ""
          c.source.map(s => {
            source=source+s
          })
          c.source = source
        }
      })
    }
    setPreviewTask(task)
  } 



  const BuildTree = ({node}) => {
    if(node.children.length || (node.weightedTasks && node.weightedTasks.length)){
      return (
        <TreeItem nodeId={node.description.de} label={node.description.de} style={node.hasTasks ? {color: "#007FFF"} : {}}>
          {
            node.children && node.children.length ? (
              node.children.map(n => (
                <BuildTree node={n} />
              ))
            ) : (
              <></>
            )
          }
          {
            node.weightedTasks && node.weightedTasks.length ? 
            (
              node.weightedTasks.map(t => (
                <>
                  {t.task && t.task.metadata ?
                    (
                      <div className="competencetaskwrapper">
                        <div className="treetask">
                          {t.competenceType === "PRIMARY" ? <div className="comptype prim">PRIMARY</div> : <div className="comptype sec">SECONDARY</div>}
                          <div className="weightedtaskinfo bold">{`TaskID: `}</div>
                          <div className="weightedtaskinfo">{t.task.metadata.task_id}</div>
                          <div className="weightedtaskinfo bold">{`Weight: `}</div>
                          <div className="weightedtaskinfo">{t.weight}</div>
                        </div>
                          <div className="flex" />
                          <img className="previewbutton" src="images/auge.png" onClick={() => prepairPreview(t.task)}/>
                          <img src="images/bleistift.png" className="editbutton" onClick={() => router.push("/task?id="+t.id)} />
                          {
                            ctx.addedIds.includes(t.task.id) ?
                            (
                              <div className="addbutton" onClick={() => ctx.removeTask(t.task)}>-</div>
                            ) : (
                              <div className="addbutton" onClick={() => ctx.addTask(t.task)}>+</div>
                            )
                          } 
                      </div>
                    ) : (
                      <></>
                    )
                  }
                </>
              ))
            ) : (
              <></>
            )
          }
        </TreeItem>
      )
    }
    return (
      <>
        <TreeItem nodeId={node.description.de} label={node.description.de} style={{color: "#575757"}} />
      </>
    )
  }

  return (
    <div className="popupwrapper">
      <div className="popupwindow">
        <img className="popupcloseimg" src="images/cancel.png" onClick={() => ctx.toggle()} />
        <div className="addtaskpopupwrapper">
          <div className="pageheading">Add Tasks</div>
          <div className="addtaskpopupcontent">
            <div className="addtasksearchwrapper">
              <div className="searchtypebuttongroup">
                <div className={visibleSection === "tasks" ? "seatchtypebutton selected" : "seatchtypebutton"} onClick={toggleVisibleSection}>Tasks</div>
                <div className={visibleSection === "competences" ? "seatchtypebutton selected" : "seatchtypebutton"} onClick={toggleVisibleSection}>Competences</div>
              </div>
              {visibleSection === "tasks" ?
              (<div className="addTaskSection">
                <div className="addtasksearchgroup">
                  <input className="input fullwidth" id="searchQuery" onChange={(e) => setTaskSearchTerm(e.target.value)} placeHolder="Search tasks (e.g. title, task-ID, ...)" />
                  <select onChange={(e) => setLanguage(e.target.value)}>
                    <option value="all">All</option>
                    <option value="de">DE</option>
                    <option value="en">EN</option>
                  </select>
                </div>
                <div className="addtasksuggestionlist">
                  {
                    tasks.map(t => (
                      <div className="notebooktaskwrapper" key={t.id}>
                        <div className="notebooktasktitle">{t.title + ` [` + t.metadata.max_points + ` Punkte]`}</div>
                        <div className="inputheading dark margintop">Task-ID</div>
                        <div className="notebooktaskid">{t.metadata.task_id}</div>
                        <div className="cellbuttonpanel">
                          {
                            ctx.addedIds.includes(t.id) ?
                            (
                              <div className="addbutton" onClick={() => ctx.removeTask(t)}>-</div>
                            ) : (
                              <div className="addbutton" onClick={() => ctx.addTask(t)}>+</div>
                            )
                          }
                          <img className="previewbutton" src="images/auge.png" onClick={() => prepairPreview(t)}/>
                          <img src="images/bleistift.png" className="editbutton" onClick={() => router.push("/task?id="+t.id)} />
                        </div>
                      </div>
                    ))
                  }
                </div>
              </div>):(
              <div className="addTaskSection">
                <input className="input fullwidth" id="searchQuery" onChange={(e) => setCompSearchTerm(e.target.value)} placeHolder="Kompetenzen..." />
                <div className="scrollview">
                  <TreeView
                    defaultCollapseIcon={<ExpandMoreIcon />}
                    defaultExpandIcon={<ChevronRightIcon />}
                    expanded={expandedNodes}
                    onNodeToggle={(e,nodeIds) => setExpandedNodes(nodeIds)}
                    sx={{ height: 240, flexGrow: 1, maxWidth: 400, overflowY: 'auto' }}
                  >
                    {
                      competences.map(node => (
                        <BuildTree node={node} />
                      ))
                    }
                  </TreeView>
                </div>
              </div>)}
            </div>
              
            <div className="verticalline" />
            <div className="addtaskpreview">
              {
                previewTask && previewTask.cells && previewTask.cells.length ?
                (
                  previewTask.cells.map(c => (
                    <>
                      {
                        c.cell_type === "markdown" ?
                        (
                          <ReactMarkdown components={{
                              // Use h2s instead of h1s
                              h1: 'h3',
                              h2: 'h4',
                              font: ({node, ...props}) => <span style={props && props.color ? {color: props.color} : {}} {...props} />,
                              code: ({node, ...props}) => <><code style={{marginBottom: "10px"}} {...props} /></>
                            }}
                            remarkPlugins={[remarkGfm]}
                            rehypePlugins={[rehypeRaw]}
                          >
                            {c.source}
                          </ReactMarkdown>
                        ) : c.cell_type === "code" ? (
                          <>
                            {String(c.source).charAt(0) === "#" ?
                              (
                                <div className="codesourcefield comment">{c.source}</div>
                              ) : (
                                <div className="codesourcefield">{c.source}</div>
                              )
                            }
                          </>
                        ) : (
                          <></>
                        )
                      }
                      <br />
                    </>
                  ))
                ) : (
                  <div className="previewplaceholder">
                    <div className="pphheading">Preview</div>
                    <img className="pphimage" src="images/auge.png"/>
                  </div>
                )
              }
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default popup