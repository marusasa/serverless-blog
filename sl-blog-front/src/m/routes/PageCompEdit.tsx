import { useState,useEffect } from 'react';
import { useLocation, useNavigate } from "react-router-dom";
import Loading from "../components/Loading";
import {SubmitButton, FormTitle} from "../components/FormComp";
import Constants from '../../util/Constants';

function PageCompEdit() {
	const navigate = useNavigate();
	const {state} = useLocation();
	const {pageComponentId} = state; // Read values passed on state
	
	const [type, setType] = useState('');
	const [json, setJson] = useState('');
	const [order, setOrder] = useState(0); // Set initial value
	const [enabled, setEnabled] = useState(false);
	const [loaded, setLoaded] = useState(false);
	const [inProcess,setInProcess] = useState(false);
	
	useEffect(() => {
			fetch('/mng/components/' + pageComponentId)
				.then((response) => response.json())
				.then((data) => {
					if (data.result == 'success') {
						setType(data.component.type);
						const o = JSON.parse(data.component.json);
						setJson(JSON.stringify(o,null,4));
						setOrder(data.component.order);
						setEnabled(data.component.enabled);
						setLoaded(true);
					} else {
						alert(JSON.stringify(data.messages));
					}
				})
				.catch((err) => {
					console.log(err.message);
					alert('Failed to load data.');
				});
		}, []);
		
		
	const updateComponent = async (data: string, order: number, enabled: boolean) => {
		try {
			let action = "";
			switch (type) {
				case Constants.VIEW_COMPONENT_TYPE_PROFILE_PIC: {
					action = "profile-pic";
					break;
				}
				case Constants.VIEW_COMPONENT_TYPE_LINK_LIST: {
					action = "link-list";
					break;
				}
				case Constants.VIEW_COMPONENT_TYPE_TEXT_BOX: {
					action = "text-box";
					break;
				}
				default: {
					throw "Unknown component type exception. Data not saved.";
				}
			}

			await fetch('/mng/components/' + pageComponentId + '/' + action, {
				method: 'PATCH',
				body: JSON.stringify({
					data: JSON.parse(data),
					order: order,
					enabled: enabled
				}),
				headers: {
					'Content-type': 'application/json; charset=UTF-8',
				},
			})
				.then((response) => response.json())
				.then((data) => {
					if (data.result == 'success') {
						alert('Data saved.');
						navigate('/m/components');
					} else {
						alert(JSON.stringify(data.messages));
					}
				})
				.catch((err) => {
					alert('Failed to save data.');
					console.log(err.message);
				})
				.finally(() => {
					setInProcess(false);
				});
		} catch (error) {
			alert(error);
		}
	};
	
	const deleteComp = async () => {
			   await fetch('/mng/components/' + pageComponentId, {
			      method: 'DELETE',
			      headers: {
			         'Content-type': 'application/json; charset=UTF-8',
			      },
			   })
			      .then((response) => response.json())
			      .then((data) => {
						if(data.result == 'success'){
							navigate('/m/components');					
						}else{
							alert(JSON.stringify(data.messages));
						}
			      })
			      .catch((err) => {
					alert('Failed to delete data.');
			         console.log(err.message);
			      })
				 .finally(() => {
					setInProcess(false);
				 });
			};
	
	const handleSave = (e: React.FormEvent) => {
	   e.preventDefault();
	   setInProcess(true);
	   updateComponent(json,order,enabled);
	};    
	const handleCancel = () => {
		navigate('/m/components');
	};
	const handleDelete = (e: React.FormEvent) => {
		e.preventDefault();
		if(confirm('Delete record?')){
			deleteComp();
		}
	};
	return (
		<>
			<FormTitle text="Edit Component"/>
			<Loading loaded={loaded}/>
			<div className={loaded ? 'visible' : 'invisible'}>
				<form onSubmit={handleSave}>
					<div className="flex">
						<SubmitButton text="Save" inProcess={inProcess}/>
						<button className="btn btn-sm btn-accent mr-3" onClick={handleDelete}>Delete</button>	
						<button className="btn btn-sm" onClick={handleCancel}>Cancel</button>
					</div>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Order:</span>
						</div>
						<input type="number" className="input input-bordered w-full max-w-5xl" value={order}
								onChange={(e) => setOrder(Number(e.target.value))} />
					</label>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Status:</span>
						</div>
						<select className="select select-bordered w-full max-w-xs" value={enabled === true?'true':'false'}
							onChange={(e) => setEnabled(e.target.value === 'true'?true:false)} >
							<option selected value='true'>Enable</option>
							<option value='false'>Disable</option>
						</select>
					</label>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Data:</span>
						</div>
						<textarea className="textarea textarea-bordered w-full max-w-5xl" value={json}
								onChange={(e) => setJson(e.target.value)}
								rows={20}></textarea>
					</label>					
				</form>
			</div>

		</>
	)
}

export default PageCompEdit