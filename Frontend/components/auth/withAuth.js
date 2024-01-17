import React, { Component } from 'react'
import API from '/api'
import router from 'next/router'

const withAuth = (LoggedInComponent) => {
  return class LoginComp extends Component {
    constructor(props) {
      super(props)
      this.state = {
          loggedIn: false
      }
    }
    async componentDidMount() {
      const config = {
        headers: {
          authorization: localStorage.getItem("Authorization"),
          username: localStorage.getItem("username")
        }
      }
      try {
        const res = await API.get(`/user`,config)
        if(res.status === 200){
          this.setState({loggedIn: true})
        }
      } catch(e) {
        console.log(e)
        router.push("/login")
      }
    }
    /*
        <>
          {this.state.loggedIn ? ( 
            <LoggedInComponent {...this.props} />
          ) : (
            <></>
          )}
        </>
    */
    render() {
      return <LoggedInComponent {...this.props} />
    } 
  }
}

export default withAuth