import React from 'react'

const taskExistsPopUp = (ctx) => {
  const visible = ctx.visible || false
  return (
    <div className={visible ? "popupwrapper" : "popupwrapper hide"}>
      <div className="popupwindow">
      <img className="popupcloseimg" src="images/cancel.png" onClick={() => ctx.toggle()} />
        <div className="deletepopupwrapper">
          <div className="pageheading">Task already exists</div>
            <div className="deletepopuptext">There already exists a task with the given taskId and language. Would you like to overwrite and save it anyway?</div>
            <div className="deletepopupbuttongroup">
              <div className="button nooutline" onClick={() => ctx.toggle()}>Cancel</div>
              <div className="button" onClick={() => {ctx.save(); ctx.toggle()}}>Save</div>
            </div>
        </div>
      </div>
    </div>
  )
}

export default taskExistsPopUp