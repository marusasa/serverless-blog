import ViewCompProfilePic from './ViewCompProfilePic'
import ViewCompLinkList from './ViewCompLinkList'
import ViewCompTextBox from './ViewCompTextBox'
import ViewCompTags from './ViewCompTags'
import {useEffect, useState} from 'react';
import Constants from '../util/Constants';
import {ViewCompData} from '../types/MyTypes';

function SideContents() {
		
	const [componentList,setComponentList] = useState('');

	useEffect(() => {
		      fetch('/components')
		         .then((response) => response.json())
		         .then((data) => {
						if(data.result == 'success'){
							const components: ViewCompData[] = data.components;
							const list = components.map(item => {
									switch(item.type){
										case Constants.VIEW_COMPONENT_TYPE_PROFILE_PIC : {											
											const json = JSON.parse(item.json);
											return (<ViewCompProfilePic url={json.url} sizeClass={`pp-w-${json.size}`} />);
										}
										case Constants.VIEW_COMPONENT_TYPE_LINK_LIST: {
											const json = JSON.parse(item.json);
											return (<ViewCompLinkList title={json.title} nameValList={json.items} />);
											break;
										}
										case Constants.VIEW_COMPONENT_TYPE_TEXT_BOX: {
											const json = JSON.parse(item.json);
											return (<ViewCompTextBox title={json.title} text={json.text} />);
											break;
										}
										case Constants.VIEW_COMPONENT_TYPE_TAGS: {
											return (<ViewCompTags/>);
											break;
										}										
										default:{
											console.log('View Component Type not supported: ' + item.type);
											break;
										}
									}
								});
							setComponentList(list);
						}else{
							alert(JSON.stringify(data.messages));
						}
		         })
		         .catch((err) => {
		            console.log(err.message);
					alert('Failed to load componetns.');
		         });
		   }, []);
	
	return (
		<>
			<div className="py-4">
				{componentList}
				<div className="flex justify-center">
					<a href="/feed" className="link link-accent text-lg ">
						 <button className="btn btn-ghost">
							<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">
								<path strokeLinecap="round" strokeLinejoin="round" d="M12.75 19.5v-.75a7.5 7.5 0 0 0-7.5-7.5H4.5m0-6.75h.75c7.87 0 14.25 6.38 14.25 14.25v.75M6 18.75a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z" />
							</svg>
						 	Feed
						</button>
					</a>
				</div>
			</div>
			<div className="text-center text-slate-400">
				<a href="/m/">
					<button className="btn btn-ghost btn-sm m-2">Manage</button>
				</a>
			</div>
		</>
	)
}

export default SideContents