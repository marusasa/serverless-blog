import { useState,useEffect } from 'react';
import { useLocation, useNavigate } from "react-router-dom";
import Loading from "../components/Loading";
import {SubmitButton, FormTitle} from "../components/FormComp";

function TagEdit() {
	const navigate = useNavigate();
	const {state} = useLocation();
	const {tagId} = state; // Read values passed on state
	
	const [name, setName] = useState('');
	const [json, setJson] = useState('');
	const [description, setDescription] = useState(''); // Set initial value
	const [loaded, setLoaded] = useState(false);
	const [inSave,setInSave] = useState(false);
	const [inDelete,setInDelete] = useState(false);
	
	useEffect(() => {
			fetch('/mng/tags/' + tagId)
				.then((response) => response.json())
				.then((data) => {
					if (data.result == 'success') {
						setName(data.tag.name);
						const o = JSON.parse(data.tag.json);
						setJson(JSON.stringify(o,null,4));
						setDescription(data.tag.description);
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
		
		
	const updateTag = async (name: string, json: string, description: string) => {
		try {
			setInSave(true);
			await fetch('/mng/tags/' + tagId, {
				method: 'PATCH',
				body: JSON.stringify({
					articleIds: JSON.parse(json),
					name: name,
					description: description
				}),
				headers: {
					'Content-type': 'application/json; charset=UTF-8',
				},
			})
				.then((response) => response.json())
				.then((data) => {
					if (data.result == 'success') {
						alert('Data saved.');
						navigate('/m/tags');
					} else {
						alert(JSON.stringify(data.messages));
					}
				})
				.catch((err) => {
					alert('Failed to save data.');
					console.log(err.message);
				})
				.finally(() => {
					setInSave(false);
				});
		} catch (error) {
			alert(error);
			setInSave(false);
		}
	};
	
	const deleteTag = async () => {
			   await fetch('/mng/components/' + tagId, {
			      method: 'DELETE',
			      headers: {
			         'Content-type': 'application/json; charset=UTF-8',
			      },
			   })
			      .then((response) => response.json())
			      .then((data) => {
						if(data.result == 'success'){
							navigate('/m/tags');					
						}else{
							alert(JSON.stringify(data.messages));
						}
			      })
			      .catch((err) => {
					alert('Failed to delete data.');
			         console.log(err.message);
			      })
				 .finally(() => {
					setInDelete(false);
				 });
			};
	
	const handleSave = (e: React.FormEvent) => {
	   e.preventDefault();
	   updateTag(name,json,description);
	};    
	const handleCancel = () => {
		navigate('/m/tags');
	};
	const handleDelete = (e: React.FormEvent) => {
		e.preventDefault();
		if(confirm('Delete record?')){
			setInDelete(true);
			deleteTag();
		}
	};
	return (
		<>
			<FormTitle text="Edit Tag"/>
			<Loading loaded={loaded}/>
			<div className={loaded ? 'visible' : 'invisible'}>
				<form>
					<div className="flex">
						<SubmitButton text="Save" inProcess={inSave} callback={handleSave} classes="btn-sm btn-primary"/>
						<SubmitButton text="Delete" inProcess={inDelete} callback={handleDelete} classes="btn-sm btn-accent"/>
						<button className="btn btn-sm" onClick={handleCancel}>Back</button>
					</div>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Name:</span>
						</div>
						<input type="text" className="input input-bordered w-full max-w-5xl" value={name}
								onChange={(e) => setName(e.target.value)} />
					</label>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Description:</span>
						</div>
						<textarea className="textarea textarea-bordered w-full max-w-5xl" value={description}
									onChange={(e) => setDescription(e.target.value)}
									rows={5}></textarea>
					</label>									
				</form>
			</div>

		</>
	)
}

export default TagEdit;