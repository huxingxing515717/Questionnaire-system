import config from '../../config/config.js'
import requestService from './common/request.js'
const surverTypeApi = {
  async getAll (params) {
    let url = config.APISERVER.host + '/surverType/getAll'
    let res = await requestService.get(url, params)
    return res
  },
  async add (params) {
    let url = config.APISERVER.host + '/surverType/add'
    let res = await requestService.post(url, params)
    return res
  },
  async delete (params) {
    let url = config.APISERVER.host + '/surverType/delete'
    let res = await requestService.post(url, params)
    return res
  },
  async selectById (params) {
    let parm = {
      id: params
    }
    let url = config.APISERVER.host + '/surverType/selectById'
    let res = await requestService.get(url, parm)
    return res
  },
  async selectTemplateType () {
    let url = config.APISERVER.host + '/surverType/selectTemplate'
    let res = await requestService.get(url, '')
    return res
  }
}
export default surverTypeApi
