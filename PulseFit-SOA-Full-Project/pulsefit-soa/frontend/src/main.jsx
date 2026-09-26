import React,{useEffect,useState} from 'react'
import {createRoot} from 'react-dom/client'
import './style.css'

const API='http://localhost:8080/api'

function App(){
 const [token,setToken]=useState(localStorage.getItem('token')||'')
 const [login,setLogin]=useState({username:'',password:''})
 const [member,setMember]=useState({name:'',email:'',phone:'',facility:''})
 const [plan,setPlan]=useState({name:'',durationMonths:12,price:0})
 const [checkin,setCheckin]=useState({memberId:'',facility:''})
 const [members,setMembers]=useState([])
 const [plans,setPlans]=useState([])
 const [message,setMessage]=useState('')

 const auth={headers:{'Content-Type':'application/json',...(token?{Authorization:`Bearer ${token}`}:{})}}
 const call=async(url,opt={})=>{
   const r=await fetch(API+url,{...opt,headers:{...auth.headers,...(opt.headers||{})}})
   const data=await r.json().catch(()=>({}))
   if(!r.ok) throw new Error(data.message||`HTTP ${r.status}`)
   return data
 }
 const load=async()=>{
   try{setMembers(await call('/members'));setPlans(await call('/plans'))}catch(e){setMessage(e.message)}
 }
 useEffect(()=>{if(token)load()},[token])

 const doLogin=async()=>{
   try{
    const d=await fetch(API+'/auth/login',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(login)})
    const x=await d.json(); if(!d.ok) throw new Error(x.message)
    localStorage.setItem('token',x.token);setToken(x.token);setMessage('Login successful')
   }catch(e){setMessage(e.message)}
 }
 const register=async()=>{
   try{await call('/auth/register',{method:'POST',body:JSON.stringify(login)});setMessage('Registered. Now login.')}
   catch(e){setMessage(e.message)}
 }
 const createMember=async()=>{
   try{await call('/members',{method:'POST',body:JSON.stringify(member)});setMessage('Member created');setMember({name:'',email:'',phone:'',facility:''});load()}
   catch(e){setMessage(e.message)}
 }
 const createPlan=async()=>{
   try{await call('/plans',{method:'POST',body:JSON.stringify({...plan,durationMonths:Number(plan.durationMonths),price:Number(plan.price)})});setMessage('Plan created');load()}
   catch(e){setMessage(e.message)}
 }
 const doCheckin=async()=>{
   try{await call('/attendance/checkin',{method:'POST',body:JSON.stringify({memberId:Number(checkin.memberId),facility:checkin.facility})});setMessage('Attendance recorded')}
   catch(e){setMessage(e.message)}
 }

 if(!token) return <main><div className="card login"><h1>PulseFit</h1><p>Fitness Membership & Attendance</p>
  <input placeholder="Username" value={login.username} onChange={e=>setLogin({...login,username:e.target.value})}/>
  <input placeholder="Password" type="password" value={login.password} onChange={e=>setLogin({...login,password:e.target.value})}/>
  <button onClick={doLogin}>Login</button><button className="secondary" onClick={register}>Register</button>
  <small>{message}</small>
 </div></main>

 return <main><header><h1>PulseFit Dashboard</h1><button onClick={()=>{localStorage.clear();setToken('')}}>Logout</button></header>
 <p className="message">{message}</p>
 <section className="grid">
  <div className="card"><h2>Add Member</h2>
   {Object.keys(member).map(k=><input key={k} placeholder={k} value={member[k]} onChange={e=>setMember({...member,[k]:e.target.value})}/>)}
   <button onClick={createMember}>Save Member</button>
  </div>
  <div className="card"><h2>Create Plan</h2>
   <input placeholder="Plan name" value={plan.name} onChange={e=>setPlan({...plan,name:e.target.value})}/>
   <input placeholder="Duration months" type="number" value={plan.durationMonths} onChange={e=>setPlan({...plan,durationMonths:e.target.value})}/>
   <input placeholder="Price" type="number" value={plan.price} onChange={e=>setPlan({...plan,price:e.target.value})}/>
   <button onClick={createPlan}>Save Plan</button>
  </div>
  <div className="card"><h2>Check In</h2>
   <select value={checkin.memberId} onChange={e=>setCheckin({...checkin,memberId:e.target.value})}><option value="">Select member</option>{members.map(m=><option key={m.id} value={m.id}>{m.id} - {m.name}</option>)}</select>
   <input placeholder="Facility" value={checkin.facility} onChange={e=>setCheckin({...checkin,facility:e.target.value})}/>
   <button onClick={doCheckin}>Record Attendance</button>
  </div>
 </section>
 <section className="card"><h2>Members</h2><table><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Facility</th></tr></thead><tbody>{members.map(m=><tr key={m.id}><td>{m.id}</td><td>{m.name}</td><td>{m.email}</td><td>{m.facility}</td></tr>)}</tbody></table></section>
 <section className="card"><h2>Plans</h2><table><thead><tr><th>ID</th><th>Name</th><th>Months</th><th>Price</th></tr></thead><tbody>{plans.map(p=><tr key={p.id}><td>{p.id}</td><td>{p.name}</td><td>{p.durationMonths}</td><td>₹{p.price}</td></tr>)}</tbody></table></section>
 </main>
}
createRoot(document.getElementById('root')).render(<App/>)
