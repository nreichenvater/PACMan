import React from 'react'
import Layout from '@/components/layouts'

const index = () => {
  return (
    <Layout activeElement="search">
      <div className="indexwrapper">
        <div className="section">
          <div className="pageheading marginbottom">Search</div>
          <div className="searchwrapper">
            <input type="text" className="input fullwidth" placeHolder="Aufgaben, Aufgabenblätter, Kompetenzen, ..." />
            <select id="language" >
              <option value="all">All</option>
              <option value="tasks">Tasks</option>
              <option value="notebooks">Notebooks</option>
              <option value="comp">Competences</option>
            </select>
          </div>
        </div>
        <div className="section">
          <div className="searchresultcontainer">
            <div className="searchresultbox red">
              <div className="searchresultheading">Task</div>
              <div className="notebooktasktitle">If-Else Variants - Shipping Cost Calculator [4 Punkte]</div>
              <div className="inputheading dark margintop">Task-ID</div>
              <div className="notebooktaskid">Branches_IfElseVariants</div>
            </div>
            <div className="searchresultbox yellow">
              <div className="searchresultheading">Notebook</div>
              <div className="notebooktasktitle">Assignment 4 - Loops</div>
              <div className="inputheading dark margintop">Assignment-ID</div>
              <div className="notebooktaskid">WS22_Assignment4</div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  )
}

export default index