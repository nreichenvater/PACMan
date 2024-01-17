import React, { useEffect, useState } from 'react'

const editCellPopUp = ({ task, cellIndexToEdit, toggle, updateTask }) => {
  useEffect(() => {
    document.body.style.overflow = 'hidden'
    return () => document.body.style.overflow = 'unset'
  }, [])
  useEffect(() => {
    if(task && task.cells){
      setCell(task.cells[cellIndexToEdit])
    }
  }, [task])
  const [cell, setCell] = useState({
    type: "",
    source: "",
    metadata: { element_type: "" }
  })
  const handleChange = (e) => {
    if(e){
      const {id, value} = e.target
      setCell({...cell,[id]:value})
    }
  }
  return (
    <div className="popupwrapper">
      <div className="popupwindow">
        <img className="popupcloseimg" src="images/cancel.png" onClick={() => toggle()} />
        <div className="editcellpopupwrapper">
          <div className="newcellheading">Element bearbeiten</div>
          <div className="editcellpopupcontent">
            <div className="cellinputrow">
              <div className="selectsection smallgap">
                <div className="inputheading">Type</div>
                <select className="tasktypeselect" id="type" onChange={handleChange} value={cell.type} >
                  <option value="markdown">markdown</option>
                  <option value="code">code</option>
                  <option value="raw">raw</option>
                </select>
              </div>
              <div className="inputsection">
                <div className="inputheading">element_type</div>
                <input id="elementtype" className="input elementtype" placeHolder="element_type" value={cell.metadata.element_type}
                  onChange={(e) => setCell({...cell, metadata: { ...cell.metadata, element_type: e.target.value}})}
                />
              </div>
              <div className="inputsection">
                <div className="inputheading">language</div>
                <input id="elementtype" className="input elementtype" placeHolder="en" value={cell.metadata.language}
                  onChange={(e) => setCell({...cell, metadata: { ...cell.metadata, language: e.target.value}})}
                />
              </div>
            </div>
            <div className="inputsection">
              <div className="inputheading">Source</div>
              <textarea id="source" className="input cellsource" placeHolder="Inhalt" rows="5" onChange={handleChange} value={cell.source} />
            </div>
            <div className="saveabortbuttongroup">
              <div className="button" onClick={() => toggle()}>Abbrechen</div>
              <div className="button accent" onClick={() => {
                const t = task
                task.cells[cellIndexToEdit] = cell
                updateTask(t)
                toggle()
              }}>Speichern</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default editCellPopUp