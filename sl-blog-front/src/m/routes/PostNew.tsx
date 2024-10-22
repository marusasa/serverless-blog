import { useState } from 'react';
import {SubmitButton, FormTitle} from "../components/FormComp";
import {  useNavigate } from "react-router-dom";

function PostNew() {

	const [title, setTitle] = useState('');
	const [body, setBody] = useState('');
	const [status, setStatus] = useState('draft'); // Set initial value
	const [inProcess,setInProcess] = useState(false);
	const navigate = useNavigate();
	
	const addPosts = async (title: string, body: string, status: string) => {
		await fetch('/mng/articles', {
			method: 'POST',
			body: JSON.stringify({
				title: title,
				body: body,
				status: status
			}),
			headers: {
				'Content-type': 'application/json; charset=UTF-8',
			},
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					alert('Data saved.');
					navigate('/m/posts');
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				console.log(err.message);
			})
			.finally(() => {
				setInProcess(false);
			});
	};
	
	const handleSave = (e: React.FormEvent) => {
		e.preventDefault();
		setInProcess(true);
		addPosts(title, body, status);
	};    
	
	return (
		<>
			<FormTitle text="New Post"/>
			<form onSubmit={handleSave}>
				<div className="flex">
					<SubmitButton text="Save" inProcess={inProcess}/>
				</div>
				<label className="form-control mb-4">
					<div className="label">
						<span className="label-text">Title:</span>
					</div>
					<input type="text" className="input input-bordered w-full max-w-5xl" value={title}
							onChange={(e) => setTitle(e.target.value)} />
				</label>
				<div className="form-control ">
					<label className="label cursor-pointer w-28">
						<span className="label-text">Draft</span>
						<input type="radio" name="status" value="draft" className="radio radio-primary" 
							checked={status === 'draft'}  
							onChange={(e) => setStatus(e.target.value)} />
					</label>
				</div>
				<div className="form-control mb-4">
					<label className="label cursor-pointer w-28">
						<span className="label-text">Publish</span>
						<input type="radio" name="status" value="publish" className="radio radio-primary"
							checked={status === 'publish'}
							onChange={(e) => setStatus(e.target.value)}  />
					</label>
				</div>	
				<label className="form-control mb-4">
					<div className="label">
						<span className="label-text">Body:</span>
					</div>
					<textarea className="textarea textarea-bordered w-full max-w-5xl " value={body}
							onChange={(e) => setBody(e.target.value)} rows={20}></textarea>
				</label>
				
			</form>

		</>
	)
}

export default PostNew