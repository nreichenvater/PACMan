import React, { useEffect, useState } from 'react'
import NavBar from '@/components/navbar'
import withAuth from '@/components/auth/withAuth'
import { ToastContainer } from "react-toastify"
import 'react-toastify/dist/ReactToastify.css'
import { PacmanLoader } from 'react-spinners'

const mainLayout = (ctx) => {
  const [loading, setLoading] = useState(false)
  useEffect(() => {
    setLoading(ctx.loading)
  }, [ctx.loading])
  return (
    <div className="mainlayout">
      <NavBar activeElement={ctx.activeElement}/>
      <div className="childcontainer">
        {loading ? <div className="loadingwrapper"><PacmanLoader color="#4fa874" loading={true} /></div> : <></>}
        {ctx.children}
      </div>
      <ToastContainer
          position="top-center"
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          draggable
          pauseOnHover
        />
    </div>
  )
}

export default withAuth(mainLayout)