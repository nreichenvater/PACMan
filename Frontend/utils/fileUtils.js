export default {
  save: async (title, type, data) => {
    try {
      const file = new Blob([data], { type })
      console.log("file ", file)
      const pickerOptions = {
        suggestedName: type === 'application/json' ? title + `.ipynb` : title,
        types: [
          {
            description: ''
          }
        ]
      }
      const fileHandle = await window.showSaveFilePicker(pickerOptions)
      const writableFileStream = await fileHandle.createWritable()
      await writableFileStream.write(file)
      await writableFileStream.close()
    } catch(e) {
      //
    }
  },
  toBase64: (arrayBuffer) => {
      return btoa(
        new Uint8Array(arrayBuffer)
          .reduce((data, byte) => data + String.fromCharCode(byte), '')
      )
  }
}