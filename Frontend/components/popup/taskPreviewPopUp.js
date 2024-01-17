import React, { useEffect, useState } from 'react'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import rehypeRaw from 'rehype-raw'

const taskPreviewPopUp = (ctx) => {
  const [obj, setObj] = useState({})
  useEffect(() => {
    if(!ctx.task.json){return}
    const obj = JSON.parse(ctx.task.json)
    if(obj.cells){
      obj.cells.forEach(c => {
        if(Array.isArray(c.source)){
          let source = ""
          c.source.map(s => {
            source=source+s
          })
          c.source = source
        }
      })
    }
    console.log(obj)
    setObj(obj)
  }, [ctx])
  return (
    <div className="popupwrapper">
      <div className="popupwindow">
        <img className="popupcloseimg" src="images/cancel.png" onClick={() => ctx.toggle()} />
        <div className="addtaskpopupwrapper">
          <div className="pageheading">Task preview</div>
            <div className="addtaskpreview">
            {
              obj && obj.cells && obj.cells.length ?
              (
                obj.cells.map(c => (
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
                <></>
              )
            }
            </div>
        </div>
      </div>
    </div>
  )
}

export default taskPreviewPopUp