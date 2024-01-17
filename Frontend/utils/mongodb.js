import { MongoClient } from 'mongodb'

const url = process.env.NODE_ENV === "development" ? "mongodb://127.0.0.1:27017" : "mongodb://mongodb:27017"
const dbName = process.env.MONGO_DB || "pacman"

console.log(url)
console.log(dbName)

export default {
  async getCollection(collection) {
    try {
      const res = await this.db.collection(collection)
      console.log("getCollection res: " + res)
      return res
    } catch (e) {
      console.warn(e.message)
      if (this.client) {
        client.close()
      }
      console.log("Create new connection.")
      this.client = await MongoClient.connect(url, {
        useUnifiedTopology: true,
      })
      this.db = this.client.db(dbName)
      return await this.db.collection(collection)
    }
  },
  async find(collection, q) {
    const coll = await this.getCollection(collection)
    return await coll.find(q).toArray()
  }/*,
  async count(collection, q) {
    const coll = await this.getCollection(collection)
    return await coll.countDocuments(q)
  }*/
}