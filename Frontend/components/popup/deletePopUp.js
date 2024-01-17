import React from 'react'

const deletePopUp = (ctx) => {
  const visible = ctx.visible || false
  return (
    <div className={visible ? "popupwrapper" : "popupwrapper hide"}>
      <div className="popupwindow">
      <img className="popupcloseimg" src="images/cancel.png" onClick={() => ctx.toggle()} />
        <div className="deletepopupwrapper">
          <div className="pageheading">{ctx.heading || ""}</div>
            <div className="deletepopuptext">{ctx.text || ""}</div>
            <div className="deletepopupbuttongroup">
              <div className="button nooutline" onClick={() => ctx.toggle()}>Cancel</div>
              <div className="button" onClick={() => {ctx.delete(); ctx.toggle()}}>Delete</div>
            </div>
        </div>
      </div>
    </div>
  )
}

export default deletePopUp