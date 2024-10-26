import { useState,useEffect } from 'react';
import { useLocation, useNavigate } from "react-router-dom";
import Loading from "../components/Loading";
import {SubmitButton, FormTitle} from "../components/FormComp";

function PostEdit() {
	const navigate = useNavigate();
	const {state} = useLocation();
	const {articleId} = state; // Read values passed on state
	
	const [title, setTitle] = useState('');
	const [body, setBody] = useState('');
	const [status, setStatus] = useState('draft'); // Set initial value
	const [aiSummary, setAiSummary] = useState('');
	const [loaded, setLoaded] = useState(false);
	const [inProcess,setInProcess] = useState(false);
	const [postChanged, setPostChanged] = useState(false);
	
	//first load article
	useEffect(() => {
		fetch('/mng/articles/' + articleId)
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setTitle(data.article.title);
					setBody(data.article.body);
					setStatus(data.article.status);
					setAiSummary(data.article.summary);
					setLoaded(true);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load article.');
			});
	}, []);
		
		
	const updatePost = async (title: string, body: string, status: string) => {
		await fetch('/mng/articles/', {
			method: 'PATCH',
			body: JSON.stringify({
				title: title, body: body, status: status, articleId: articleId, summary: aiSummary
			}),
			headers: {
				'Content-type': 'application/json; charset=UTF-8',
			},
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					alert('Data saved.');
					setPostChanged(false);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				alert('Failed to save article.');
				console.log(err.message);
			})
			.finally(() => {
				setInProcess(false);
			});
	};
	
	const deletePost = async () => {
		await fetch('/mng/articles/' + articleId, {
			method: 'DELETE',
			headers: {
				'Content-type': 'application/json; charset=UTF-8',
			},
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					navigate('/m/posts');
				} else {
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
	
	const getAiSummary = async () => {
			await fetch('/mng/articles/' + articleId + "/ai-summary", {
				method: 'GET',
				headers: {
					'Content-type': 'application/json; charset=UTF-8',
				},
			})
				.then((response) => response.json())
				.then((data) => {
					if (data.result == 'success') {
						setAiSummary(data.summary);
						setPostChanged(true);
						alert('AI Summary generated (Not saved yet). Summary: ' + data.summary);
					} else {
						alert(JSON.stringify(data.messages));
					}
				})
				.catch((err) => {
					alert('Error.');
					console.log(err.message);
				})
				.finally(() => {
				});
		};
	
	const handleSave = (e: React.FormEvent) => {
		e.preventDefault();
		setInProcess(true);
		updatePost(title, body, status);
	};
	const handleCancel = (e: React.FormEvent) => {
		e.preventDefault();
		let go = false;
		if(postChanged){
			go = confirm('There are unsaved changes. Disregard and go back?');
		}else{
			go = true;
		}
		if(go){
			navigate('/m/posts');
		}
	};
	const handleDelete = (e: React.FormEvent) => {
		e.preventDefault();
		if(confirm('Delete record?')){
			deletePost();
		}
	};
	const handleGenAiSummary = (e: React.FormEvent) => {
			e.preventDefault();
			if(postChanged){
				alert('There are unsaved changes. Data must be saved first.');
			}else{
				getAiSummary();
			}
		};
	const handleViewAiSummary = (e: React.FormEvent) => {
		e.preventDefault();
		alert('AI Summary: ' + aiSummary);
	};
	
	return (
		<>			
			<FormTitle text="Edit Post"/>
			<Loading loaded={loaded}/>
			<div className={loaded ? 'visible' : 'invisible'}>
				<form onSubmit={handleSave}>
					<div className='flex'>
						<SubmitButton text="Save" inProcess={inProcess}/>
						<button className="btn btn-sm btn-accent mr-3" onClick={handleDelete}>Delete</button>	
						<button className="btn btn-sm mr-3" onClick={handleCancel}>Back</button>
						<button className="btn btn-sm btn-outline btn-secondary mr-3" onClick={handleGenAiSummary}>Generate AI Summary</button>		
						<button className="btn btn-sm btn-outline btn-secondary mr-3" onClick={handleViewAiSummary}>View AI Summary</button>			
					</div>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Title:</span>
						</div>
						<input type="text" className="input input-bordered w-full max-w-5xl" value={title}
								onChange={(e) => {setTitle(e.target.value);setPostChanged(true);}} />
					</label>
					<div className="form-control ">
						<label className="label cursor-pointer w-28">
							<span className="label-text">Draft</span>
							<input type="radio" name="status" value="draft" className="radio radio-primary" 
								checked={status === 'draft'}  
								onChange={(e) => {setStatus(e.target.value);setPostChanged(true);}} />
						</label>
					</div>
					<div className="form-control mb-4">
						<label className="label cursor-pointer w-28">
							<span className="label-text">Publish</span>
							<input type="radio" name="status" value="publish" className="radio radio-primary"
								checked={status === 'publish'}
								onChange={(e) => {setStatus(e.target.value);setPostChanged(true);}}  />
						</label>
					</div>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Article:</span>
						</div>
						<textarea className="textarea textarea-bordered w-full max-w-5xl" value={body}
								onChange={(e) => {setBody(e.target.value);setPostChanged(true);}} rows={20}></textarea>
					</label>
					
				</form>
			</div>

		</>
	)
}

export default PostEdit