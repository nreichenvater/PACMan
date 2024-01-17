import API from 'api'
import JSZip from 'jszip'
import FileSaver from 'file-saver'

export default {
    downloadPackage: async (notebook, title, json) => {
        const zip = JSZip()
        zip.file(title+`.ipynb`, new Blob([json], { type: 'application/json'}))
        for(let i=0; i<notebook.tasks.length; i++){
            if(notebook.tasks[i].file){
                try {
                    const res = await API.get(`/file/`+notebook.tasks[i].file)
                    if(res.data.fileName && res.data.fileName != "undefined"){
                        zip.file(res.data.fileName, new Blob([new Uint8Array(res.data.binContent.data)], { type: res.data.type }))
                    }
                } catch(e) {
                    console.log(e)
                }
            }
        }
        zip.generateAsync({type: 'blob'}).then(zipFile => {
            const currentDate = new Date().getTime()
            const fileName = title+`.zip`
            return FileSaver.saveAs(zipFile, fileName)
        })
    },
    downloadExistingNotebook: async (notebook) => {
        const zip = JSZip()
        zip.file(notebook.title+`.ipynb`, new Blob([notebook.json], { type: 'application/json'}))
        if(notebook.fileIds && notebook.fileIds.length){
            for(let i=0; i<notebook.fileIds.length; i++){
                if(notebook.fileIds[i]){
                    try {
                        const res = await API.get(`/file/`+notebook.fileIds[i])
                        if(res.data.fileName && res.data.fileName != "undefined"){
                            zip.file(res.data.fileName, new Blob([new Uint8Array(res.data.binContent.data)], { type: res.data.type }))
                        }
                    } catch(e) {
                        console.log(e)
                    }
                }
            }
        }
        zip.generateAsync({type: 'blob'}).then(zipFile => {
            const currentDate = new Date().getTime()
            const fileName = notebook.title+`.zip`
            return FileSaver.saveAs(zipFile, fileName)
        })
    }
}
