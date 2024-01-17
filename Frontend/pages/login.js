import React, { useState } from 'react'
import API from '/api'
import router from 'next/router'
import { PacmanLoader } from 'react-spinners'

const login = () => {
  const [state, setState] = useState({
    error: "",
    user: "",
    password: ""
  })
  const [loading, setLoading] = useState(false)
  const onChange = (e) => {
    const {id,value} = e.target
    setState({...state, [id]: value})
  }
  const login = async () => {
    setLoading(true)
    if(!state.user || !state.password){
      setState({...state,error:"Please enter username and password"})
      return
    } else {
      setState({...state,error:""})
    }
    const body = {
      username: state.user,
      password: state.password
    }
    try {
      const res = await API.post(`/login`, body)
      if(res.status === 200){
        localStorage.setItem('username', res.data.username)
        localStorage.setItem('Authorization', res.headers.authorization)
        setLoading(false)
        router.push("/tasks")
      }
    } catch(e) {
      setLoading(false)
      if(e.response && e.response.data && e.response.data.error){
        setState({...state,error:e.response.data.error}) 
      } else {
        setState({...state,error:"an error occurred"})
      }
    }
  }
  const handleKeyUp = (e) => {
    if(e){
      e.preventDefault()
      if(e.key === 'Enter'){
        login()
      }
    }
  }
  return (
    <div className="login">
      {loading ? <div className="loadingwrapper"><PacmanLoader color="#4fa874" loading={true} /></div> : <></>}
      <div className="loginwrapper" onKeyUp={handleKeyUp}>
        <div className="loginleft">
          <div className="loginheading">Login</div>
          {state.error ? <div className="errorlabel">{state.error}</div> : <></>}
          <input type="text" placeholder="Username" id="user" className="input fullwidth" onChange={onChange}/>
          <input type="password" placeholder="Password" id="password" className="input fullwidth" onChange={onChange}/>
          <div className="loginbutton" onClick={login}>Log in</div>
        </div>
        <div className="loginright"><img className="logo logon" src="images/logo.png" /></div>
      </div>
    </div>
  )
}

export default login