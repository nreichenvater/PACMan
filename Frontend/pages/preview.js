import React, { useEffect, useState } from 'react'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import rehypeRaw from 'rehype-raw'

export async function getServerSideProps(ctx) {
  if(ctx.code && ctx.type){
    const type = ctx.query.type
    const code = ctx.query.code
    return { props: { type, code } }
  }
  return { props:  {} }
}

const preview = (ctx) => {
  const code = ctx.code || {}
  const [json,setJson] = useState({})

  useEffect(() => {
    const json = localStorage.getItem('previewJson')
    if(json){
      const obj = JSON.parse(json)
      if(obj.cells){
        obj.cells.forEach(c => {
          if(Array.isArray(c.source)){
            console.log("cell: ", c)
            let source = ""
            if(c.cell_type === "markdown"){
              if(c.metadata.exercise_data && c.metadata.exercise_data === "grading_table"){
                c.source.map(s => {
                  source=source+s.trim()+"\n"
                })
              } else {
                c.source.map(s => {
                  source=source+s
                })
              }
            } else if(c.cell_type === "code"){
              c.source.map(s => {
                source=source+s
              })
            }
            //source gets changed from string array to single string
            c.source = source
          }
        })
        console.log(obj)
      }
      setJson(obj)
    }
  }, [])

  return (
    <div className="previewwrapper">
      {
        json && json.cells ?
        (
          json.cells.map(c => (
            <>
              {
                c.cell_type === "markdown" ?
                (
                  <ReactMarkdown components={{
                      h1: 'h3',
                      h2: 'h4',
                      font: ({node, ...props}) => <span style={props && props.color ? {color: props.color} : {}} {...props} />,
                      code: ({node, ...props}) => <code style={{marginBottom: "10px"}} {...props} />
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
  )
}

export default preview