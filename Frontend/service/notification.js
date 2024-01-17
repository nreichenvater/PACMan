import { toast } from "react-toastify"
import _ from "lodash"
export default {
  error(e) {
    let message = _.get(e, "response.data.error.message")
    if (!message) {
      message = _.get(e, "response.data.error", e.message)
    }
    if (!message) {
      message = e;
    }
    toast.error(message)
  },
  success(text = "success") {
    toast.success(text)
  }
}