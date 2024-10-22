import ViewCompProfilePic from './ViewCompProfilePic'
import ViewCompLinkList from './ViewCompLinkList'
import ViewCompTextBox from './ViewCompTextBox'
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
			</div>
			<div className="py-4 text-center text-slate-400">
				<a href="/m/">
					<button className="btn btn-ghost btn-sm m-2">Manage</button>
				</a>
			</div>
		</>
	)
}

export default SideContents