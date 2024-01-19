export default {
    moveTags: (json) => {
      json.cells.forEach(c => {
        if(c.metadata.tags){
          c.tags = c.metadata.tags
          delete c.metadata.tags
        }
      })
      return json
    },
    moveElem: (elem, mode) => {
        const index = task.cells.indexOf(elem)
        const cells = task.cells
        if(mode === 1){
          arrayUtils.swap(cells,index,index-1)
        } else if(mode === 2){
          arrayUtils.swap(cells,index,index+1)
        }
        return {...task,cells}
    },
    validateTaskInput: (task) => {
        if(!task.title){
          return "Please enter a title for the task"
        }
        if(!task.cells.length){
          return "Please add cells to the tasks"
        }
        if(!task.taskId){
          return "Please enter a (per language) unique task_id"
        }
        if(!task.maxPoints || isNaN(task.maxPoints)){
          return "Please enter a valid number of default max points"
        }
    }
}