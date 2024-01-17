import React from 'react'
import Link from 'next/link'
import router from 'next/router'

const NavBar = (ctx) => {
  return (
    <div className="navbarwrapper">
      <img className="logo nav" src="images/logo.png" onClick={() => router.push("/tasks")}/>
      
      <div className="navgroupheading">Tasks</div>
      <Link href="/tasks">
        <div className={ctx.activeElement && ctx.activeElement === "tasks" ? "navbaritem selected" : "navbaritem"}>
          <img className="menuicon" src="images/web-programming.png" />
          Tasks
        </div>
      </Link>
      <Link href="/notebooks">
        <div className={ctx.activeElement && ctx.activeElement === "notebooks" ? "navbaritem selected" : "navbaritem"}>
          <img className="menuicon" src="images/contract.png" />
          Notebooks
        </div>
      </Link>
      <div className="navgroupheading">Competences</div>
      <Link href="/competences">
        <div className={ctx.activeElement && ctx.activeElement === "competences" ? "navbaritem selected" : "navbaritem"}>
          <img className="menuicon" src="images/task-list.png" />
          Competences
        </div>
      </Link>
      <div className="navgroupheading">Others</div>
      <Link href="/tags">
        <div className={ctx.activeElement && ctx.activeElement === "tags" ? "navbaritem selected" : "navbaritem"}>
          <img className="menuicon" src="images/price-tag.png" />
          Tags
        </div>
      </Link>
      <div className="navbaritem" onClick={() => {localStorage.clear(); router.push("/login")}}>
        <img className="menuicon" src="images/logout.png" />
        Log out
      </div>
    </div>
  )
}

export default NavBar

/*

<Link href="/tags" className={ctx.activeElement && ctx.activeElement === "password" ? "navbaritem selected" : "navbaritem"}>Change password</Link>


<div className="navgroupheading">General</div>
      <Link href="/" className={ctx.activeElement && ctx.activeElement === "search" ? "navbaritem selected" : "navbaritem"}>Overview</Link>
*/