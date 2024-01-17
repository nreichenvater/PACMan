import React, { useRef, useState, useEffect, useId } from 'react'
import Layout from '@/components/layouts'
import API from '@/api'
import Notification from '@/service/notification'
import DeletePopUp from '@/components/popup/deletePopUp'
import TreeView from '@mui/lab/TreeView'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'
import ChevronRightIcon from '@mui/icons-material/ChevronRight'
import TreeItem from '@mui/lab/TreeItem'

const competences = (ctx) => {
  const fileInputRef = useRef(null)
  const [fileType, setFileTpye] = useState("")
  const [chosenFileName, setChosenFileName] = useState("")
  const [fileContent, setFileContent] = useState("")
  const [competenceTree, setCompetenceTree] = useState([])
  const [searchTerm, setSearchTerm] = useState("912da55c-c4ae-11ed-afa1-0242ac120002") //prevent fetching initially, easier than e.g. refs because of possible double renders, react...
  const [loading, setLoading] = useState("")
  const [deletePopUpVisible, setDeletePopUpVisible] = useState(false)
  const [expanded, setExpanded] = useState([])
  const [selected, setSelected] = useState([])
  const [idsVisible, setIdsVisible] = useState(false)
  const [compLang, setCompLang] = useState("de")

  const toggleDeletePopUp = () => setDeletePopUpVisible(vis => !vis)

  useEffect(() => {
    fetchTree()
  }, [])

  const fetchTree = async () => {
    try {
      setLoading(true)
      const st = searchTerm === "912da55c-c4ae-11ed-afa1-0242ac120002" ? "" : searchTerm
      const res = await API.get(`/competence/tree?searchTerm=`+st)
      if(res.status === 200){
        setLoading(false)
        console.log(res.data.nodes)
        setCompetenceTree(res.data.nodes)
      }
    } catch(e) {
      setLoading(false)
      Notification.error(e)
    }
  }

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      if(searchTerm !== "912da55c-c4ae-11ed-afa1-0242ac120002"){
        fetchTree()
      }
    }, 1000)
    return () => clearTimeout(delayDebounceFn)
  }, [searchTerm])

  const fileChange = (e) => {
    const file = e.target.files[0]
    console.log(file)
    if (!file) {
      return
    }
    setChosenFileName(file.name)
    const type = file.type
    const reader = new FileReader()
    reader.onload = (e) => {
      const content = JSON.parse(e.target.result)
      //edit the content for naming conventions etc
      if(fileType === "competencemodel"){
        content.competences.forEach(c => {
          c.competenceId = c.id
          delete c.id
        })
      } else if(fileType === "mappingfile"){
        content.assignmentMapping = content.assignment_mapping
        delete content.assignment_mapping
        content.assignmentMapping.tasks.forEach(t => {
          t.comp_prim = t.competencies.comp_prim
          t.comp_sec = t.competencies.comp_sec
          delete t.competencies
        })
      } else {
        Notification.error("The file does not contain the necessary data")
        return
      }
      setFileContent(content)
    }
    reader.readAsText(file)
  }

  const openUpload = (e) => {
    if(e){
      const val = e.target.value
      if(val !== "choose"){
        setFileTpye(val)
        //document.getElementById("filetype").value = "choose"
        fileInputRef.current.click()
      }
    }
  }

  const save = async (e) => {
    const url = fileType === "competencemodel" ? "/competence/model" : fileType === "mappingfile" ? "/competence/mapping" : ""
    try {
      setLoading(true)
      const res = await API.post(url, fileContent)
      if(res.status === 200){
        const type = fileType === "competencemodel" ? "Competence model" : fileType === "mappingfile" ? "Mapping-File" : ""
        fetchTree()
        setLoading(false)
        Notification.success("The " + type + " was saved successfully")
      }
    } catch(e) {
      setLoading(false)
      Notification.error(e)
    }
    document.getElementById("filetype").value = "choose"
  }

  const deleteMappings = async () => {
    try {
      setLoading(true)
      const res = await API.delete(`/competence/mapping`)
      if(res.status === 200){
        fetchTree()
        setLoading(false)
        Notification.success("The model and the mappings were removed successfully")
      }
    } catch(e) {
      setLoading(false)
      Notification.error(e)
    }
  }

  const handleToggle = (e, nodeIds) => {
    setExpanded(nodeIds)
  }

  const handleSelect = (e, nodeIds) => {

    setSelected(nodeIds)
  }

  const handleExpandClick = () => {
    if(expanded.length){
      setExpanded([])
    } else {
      const ids = getAllIds(competenceTree)
      setExpanded(ids)
    }
  }

  const getAllIds = (ct) => {
    const ids = []
    for(let i=0; i<ct.length; i++){
      ids.push(ct[i].competenceId)
      if(ct[i].children.length){
        ids.push.apply(ids, getAllIds(ct[i].children))
      }
    }
    return ids 
  }

  const toggleLanguage = () => {
    if(compLang === "de"){
      setCompLang("en")
    } else if(compLang === "en"){
      setCompLang("de")
    }
  }

  const getDescription = (node) => {
    if(compLang === "de"){
      return node.description.de
    } else if(compLang === "en"){
      return node.description.en
    }
  }

  const toggleIds = () => setIdsVisible(vis => !vis)

  const BuildTree = ({node}) => {
    if(node.children.length || node.weightedTasks.length){
      return (
        <TreeItem nodeId={node.competenceId} label={idsVisible ? getDescription(node) + " [" + node.competenceId + "]" : getDescription(node)} style={node.hasTasks ? {color: "#007FFF"} : {}}>
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
                      <div className="treetask">
                        {t.competenceType === "PRIMARY" ? <div className="comptype prim">PRIMARY</div> : <div className="comptype sec">SECONDARY</div>}
                        <div className="weightedtaskinfo bold">{`TaskID: `}</div>
                        <div className="weightedtaskinfo">{t.task.metadata.task_id}</div>
                        <div className="weightedtaskinfo bold">{`Weight: `}</div>
                        <div className="weightedtaskinfo">{t.weight}</div>
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
      <TreeItem nodeId={node.competenceId} label={idsVisible ? getDescription(node) + " [" + node.competenceId + "]" : getDescription(node)} style={{color: "#575757"}}/>
    )
  }

  return (
    <Layout activeElement="competences" loading={loading}>
      <DeletePopUp visible={deletePopUpVisible} toggle={toggleDeletePopUp} delete={deleteMappings} heading="Delete all Mappings and Model" text={`Should all mappings and the competence model really be deleted?`}/>
      <div className="section">
        <div className="taskheaderrow competences">
          <div className="pageheading marginbottom">Competences</div>
          <div className="flex" />
          <select id="filetype" className="filetypeselect" onChange={openUpload}>
            <option value="choose">Choose</option>
            <option value="competencemodel">Competence model</option>
            <option value="mappingfile">Mapping-File</option>
          </select>
          <input type="file" className="competenceinput" ref={fileInputRef} onChange={fileChange} />
          <input type="text" disabled={true} value={chosenFileName} className="input filename" placeHolder="No file chosen" />
          <div className="button" onClick={save}>Upload</div>
        </div>
        <div className="buttonflexwrapper">
          <div className="button deletemappings" onClick={toggleDeletePopUp}>Delete mappings and model</div>
        </div>
        <div className="searchwrapper">
          <input type="text" className="input fullwidth" onChange={(e) => setSearchTerm(e.target.value)} placeholder="Competences..." />
        </div>
      </div>
      <div className="section comptree">
        {
          competenceTree && competenceTree.length ?
          (
            <>
              <div className="treebuttongroup">
                <div className="button" onClick={toggleIds}>{idsVisible ? "Hide IDs" : "Show IDs"}</div>
                <div className="button" onClick={handleExpandClick}>{expanded.length ? "Collapse all" : "Expand all"}</div>
                <div className="button" onClick={toggleLanguage}>Toggle Language</div>
              </div>
              <TreeView
                aria-label="controlled"
                defaultCollapseIcon={<ExpandMoreIcon />}
                defaultExpandIcon={<ChevronRightIcon />}
                sx={{ height: 240, flexGrow: 1, maxWidth: 400, overflowY: 'auto' }}
                expanded={expanded}
                selected={selected}
                onNodeToggle={handleToggle}
                onNodeSelect={handleSelect}
              >
              {
                competenceTree.map(node => (
                  <BuildTree node={node} />
                ))
              }
            </TreeView>
          </>
          ) : <div className="nodatatext">No competences</div>
        }
      </div>
    </Layout>
  )
}

export default competences

//aria-label="file system navigator"
//useId()