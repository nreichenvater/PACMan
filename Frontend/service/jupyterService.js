export default {
  moveTags: (json) => {
    json.cells.forEach(c => {
      if(c.metadata.tags){
        c.tags = c.metadata.tags
        delete c.metadata.tags
      }
    })
    return json
  }
}