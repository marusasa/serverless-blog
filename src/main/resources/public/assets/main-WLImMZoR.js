import{j as e,r,C as d,L as j,c as p}from"./index-CSwPi2qE.js";function g({title:t,subTitle:n}){return e.jsx(e.Fragment,{children:e.jsx("div",{className:"flex flex-row",children:e.jsxs("div",{className:"grow",children:[e.jsx("h1",{className:"text-5xl pb-5",children:t}),e.jsx("h2",{className:"text-base md:text-3xl pb-3",children:n})]})})})}function f({article:t}){const n=t.publishedAt.substring(0,10),o=new Date(n).toLocaleDateString(void 0,{timeZone:"UTC"});return e.jsx(e.Fragment,{children:e.jsx("div",{className:"card bg-white w-full mb-8 text-left shadow-lg border-solid border border-slate-200 p-4 md:p-8",children:e.jsx("div",{className:"card-body p-0",children:e.jsxs("article",{children:[e.jsx("h2",{className:"card-title",children:t.title}),e.jsx("p",{className:"font-thin italic",children:e.jsx("time",{dateTime:n,children:o})}),e.jsx("p",{className:"whitespace-pre-wrap",children:t.body})]})})})})}function N(){const[t,n]=r.useState([]);return r.useEffect(()=>{fetch("/articles").then(s=>s.json()).then(s=>{s.result=="success"?n(s.articles):alert(JSON.stringify(s.messages)),console.log(s)}).catch(s=>{console.log(s.message),alert("Failed to load articles.")})},[]),e.jsx(e.Fragment,{children:e.jsx("div",{className:"py-4",children:t.map(s=>e.jsx(f,{article:s},s.articleId))})})}function b({url:t,sizeClass:n}){return e.jsx(e.Fragment,{children:e.jsx("div",{className:"avatar mb-5 w-full place-content-center",children:e.jsx("div",{className:`${n} rounded shadow-lg `,children:e.jsx("img",{src:t})})})})}function w({title:t,nameValList:n}){const s=n.map(o=>e.jsxs("li",{className:"mb-1",children:[e.jsx("a",{href:`${o.url}`,className:"link link-accent",target:"_blank",children:o.name})," - ",o.desc]}));return e.jsx(e.Fragment,{children:e.jsxs("div",{className:"shadow-lg bg-teal-100 text-left	p-3 mb-5",children:[e.jsx("p",{className:"text-center mb-2",children:t}),e.jsx("ul",{children:s})]})})}function v({title:t,text:n}){return e.jsx(e.Fragment,{children:e.jsxs("div",{className:"shadow-lg bg-teal-100 text-left	p-3 mb-5",children:[e.jsx("p",{className:"text-center mb-2",children:t}),e.jsx("p",{className:"whitespace-pre-wrap",children:n})]})})}function S(){const[t,n]=r.useState("");return r.useEffect(()=>{fetch("/components").then(s=>s.json()).then(s=>{if(s.result=="success"){const i=s.components.map(a=>{switch(a.type){case d.VIEW_COMPONENT_TYPE_PROFILE_PIC:{const c=JSON.parse(a.json);return e.jsx(b,{url:c.url,sizeClass:`pp-w-${c.size}`})}case d.VIEW_COMPONENT_TYPE_LINK_LIST:{const c=JSON.parse(a.json);return e.jsx(w,{title:c.title,nameValList:c.items})}case d.VIEW_COMPONENT_TYPE_TEXT_BOX:{const c=JSON.parse(a.json);return e.jsx(v,{title:c.title,text:c.text})}default:{console.log("View Component Type not supported: "+a.type);break}}});n(i)}else alert(JSON.stringify(s.messages))}).catch(s=>{console.log(s.message),alert("Failed to load componetns.")})},[]),e.jsxs(e.Fragment,{children:[e.jsx("div",{className:"py-4",children:t}),e.jsx("div",{className:"py-4 text-center text-slate-400",children:e.jsx("a",{href:"/m/",children:e.jsx("button",{className:"btn btn-ghost btn-sm m-2",children:"Manage"})})})]})}function T(){const[t,n]=r.useState(""),[s,o]=r.useState(""),[i,a]=r.useState(!1),[c,m]=r.useState(!1),[h,x]=r.useState("");r.useEffect(()=>{fetch("/basic-info").then(l=>l.json()).then(l=>{l.result=="success"?(n(l.title),document.title=l.title,o(l.subTitle),a(!0),x(l.iconUrl),l.faviconUrl!=""&&k(l.faviconUrl)):alert(JSON.stringify(l.messages)),console.log(l)}).catch(l=>{console.log(l.message),alert("Failed to load articles.")})},[]);const u=l=>{l.preventDefault(),m(!c)};return e.jsx(e.Fragment,{children:e.jsxs("div",{className:"container mx-auto font-mono",children:[e.jsx(j,{loaded:i}),e.jsxs("div",{className:i?"visible":"invisible",children:[e.jsxs("div",{className:"columns-1 text-left",children:[e.jsx(g,{title:t,subTitle:s}),e.jsxs("div",{className:"flex md:hidden",children:[e.jsx(E,{iconUrl:h}),e.jsx("a",{className:"link link-accent mt-2",onClick:u,children:c?"Back to Posts":"Show About Me"})]})]}),e.jsxs("div",{className:"grid grid-cols-4",children:[e.jsx("div",{className:c?" hidden ":" col-span-4 md:col-span-3",children:e.jsx(N,{})}),e.jsx("div",{className:c?" col-span-4 ":" hidden   md:col-span-1 md:block md:pl-4",children:e.jsx(S,{})})]}),e.jsx("div",{children:e.jsxs("p",{className:"text-slate-400 text-center",children:["Powered by ",e.jsx("a",{className:"link",target:"_blank",href:"https://github.com/marusasa/serverless-blog",children:"Serverless Blog Project"})]})})]})]})})}function E({iconUrl:t}){return t!=""?e.jsx("img",{src:t,className:"w-10 rounded-full mr-4 shadow-lg"}):e.jsx("svg",{xmlns:"http://www.w3.org/2000/svg",fill:"none",viewBox:"0 0 24 24",strokeWidth:1.5,stroke:"currentColor",className:"size-10 mr-4 shadow-lg rounded-full",children:e.jsx("path",{strokeLinecap:"round",strokeLinejoin:"round",d:"M17.982 18.725A7.488 7.488 0 0 0 12 15.75a7.488 7.488 0 0 0-5.982 2.975m11.963 0a9 9 0 1 0-11.963 0m11.963 0A8.966 8.966 0 0 1 12 21a8.966 8.966 0 0 1-5.982-2.275M15 9.75a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"})})}function k(t){const n=document.createElement("link");n.rel="shortcut icon",n.type="image/png",n.href=t,document.head.appendChild(n)}p(document.getElementById("root")).render(e.jsx(r.StrictMode,{children:e.jsx(T,{})}));