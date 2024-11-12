import './App.css'
import BlogTop from './components/BlogTop.tsx'
import SideContents from './components/SideContents.tsx'
import { useEffect, useState } from 'react';
import Loading from "./m/components/Loading";
import { Outlet   } from "react-router-dom";
import AnalyticUtil from "./util/AnalyticUtil";

function App() {

	const [title, setTitle] = useState('');
	const [subTitle, setSubTitle] = useState(''); 
	const [loaded, setLoaded] = useState(false);
	const [showAboutMe, setShowAboutMe] = useState(false);
	const [iconUrl, setIconUrl] = useState('');
	const [visitorId, setVisitorId] = useState(0);
	
	useEffect(() => {
		fetch('/basic-info')
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setTitle(data.title);
					document.title = data.title; 
					setSubTitle(data.subTitle);
					setLoaded(true);
					setIconUrl(data.iconUrl);
					if(data.faviconUrl != ""){
						addFavicon(data.faviconUrl);
					}
					setVisitorId(data.visitorId);
					AnalyticUtil.init(data.visitorId);
				} else {
					alert(JSON.stringify(data.messages));
				}
				console.log(data);
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load articles.');
			});
			
	}, []);
	
	const toggleShowAboutMe = (e: React.MouseEvent<HTMLAnchorElement> ) => {
		   e.preventDefault();
		   setShowAboutMe(!showAboutMe);
		};    
	
	return (
		<>
			<div className="container mx-auto font-mono">
				<Loading loaded={loaded}/>
				<div className={loaded ? 'visible' : 'invisible'}>
					<div className="columns-1 text-left">
						<BlogTop title={title} subTitle={subTitle}></BlogTop>
						<div className="flex md:hidden">
							<ShowIcon iconUrl={iconUrl}/>
							<a className="link link-accent mt-2" onClick={toggleShowAboutMe}
									>{showAboutMe? 'Back to Posts':'Show About Me'}</a>
						</div>
					</div>
					<div className="grid grid-cols-6">
						<div className={showAboutMe? ' hidden ':'' + " col-span-6 md:col-span-4"}>							
							<Outlet context={[loaded, setLoaded]}/>
						</div>
						<div className={showAboutMe? ' col-span-6 ': ' hidden ' + "  md:col-span-2 md:block md:pl-4"}>
							<SideContents></SideContents>
						</div>
					</div>
					<div>
						<p className="text-slate-400 text-center">Powered by <a className="link" 
								target="_blank" 
								href="https://github.com/marusasa/serverless-blog">Serverless Blog Project</a>
						</p>
					</div>
				</div>
			</div>
		</>
	)
}

function ShowIcon({iconUrl}:{iconUrl:string}){
	if(iconUrl != ''){
		return <img src={iconUrl} className="w-10 rounded-full mr-4 shadow-lg"/>;
	}else{
		//return default icon.
		return (
			<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} 
					stroke="currentColor" className="size-10 mr-4 shadow-lg rounded-full">
				<path strokeLinecap="round" strokeLinejoin="round" 
						d="M17.982 18.725A7.488 7.488 0 0 0 12 15.75a7.488 7.488 0 0 0-5.982 2.975m11.963 0a9 9 0 1 0-11.963 0m11.963 0A8.966 8.966 0 0 1 12 21a8.966 8.966 0 0 1-5.982-2.275M15 9.75a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
			</svg>
		);
	}
}

function addFavicon(href: string) {
	const link = document.createElement('link');
	link.rel = 'shortcut icon';
	link.type = 'image/png';
	link.href = href;
	document.head.appendChild(link);
}

export default App
