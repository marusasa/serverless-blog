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
	const [inSave,setInSave] = useState(false);
	const [inPubSave,setInPubSave] = useState(false);
	const [inDelete,setInDelete] = useState(false);
	const [inGenAi,setInGenAi] = useState(false);
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
				setInSave(false);
				setInPubSave(false);
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
				setInDelete(false);
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
					setInGenAi(false);
				});
		};
	
	const handleSave = (e: React.FormEvent) => {
		e.preventDefault();
		setInSave(true);
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
			setInDelete(true);
			deletePost();
		}
	};
	const handleGenAiSummary = (e: React.FormEvent) => {
			e.preventDefault();
			if(postChanged){
				alert('There are unsaved changes. Data must be saved first.');				
			}else{
				setInGenAi(true);
				getAiSummary();
			}
		};
	const handlePupUnPub = (e: React.FormEvent) => {
		e.preventDefault();
		if(confirm("Save post and " + (status === 'draft'?'Publish?':'Unpublish?'))){
			let newStatus:string = 'draft';
			if(status === 'draft'){
				newStatus = 'publish';
			}
			setStatus(newStatus);
			setInPubSave(true);
			updatePost(title, body, newStatus);				
		}
	};
	const handleViewAiSummary = (e: React.FormEvent) => {
			e.preventDefault();
			alert('AI Summary: ' + aiSummary);
		};
	const handleClearAiSummary = (e: React.FormEvent) => {
		e.preventDefault();
		setAiSummary('');
		alert('AI Summary Cleared (Not Saved Yet)');
	};
	return (
		<>			
			<FormTitle text={"Edit Post : " + (status==='draft'?'Draft':'Published')}/>
			<Loading loaded={loaded}/>
			<div className={loaded ? 'visible' : 'invisible'}>
				<form>
					<div className='flex'>
						<SubmitButton text="Save" inProcess={inSave} callback={handleSave} classes="btn-sm btn-primary"/>
						<SubmitButton text={status === 'draft'?'Publish':'Unpublish'} inProcess={inPubSave} callback={handlePupUnPub} classes="btn-sm btn-secondary"/>
						<button className="btn btn-sm mr-3" onClick={handleCancel}>Back</button>	
						<SubmitButton text="Delete" inProcess={inDelete} callback={handleDelete} classes="btn-sm btn-accent"/>
					</div>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Title:</span>
						</div>
						<input type="text" className="input input-bordered w-full max-w-5xl" value={title}
								onChange={(e) => {setTitle(e.target.value);setPostChanged(true);}} />
					</label>
					<div className="mb-4">
						<div className="label">
							<span className="label-text">AI Summary:</span>
						</div>
						<div>
							<button className={aiSummary === ""?'hidden':'' + " btn btn-sm btn-outline btn-secondary mr-3"}
									onClick={handleViewAiSummary}>View AI Summary</button>
							<SubmitButton text="Generate AI Summary" inProcess={inGenAi} 
									callback={handleGenAiSummary} classes="btn-sm btn-outline btn-secondary"/>
							<button className={aiSummary === ""?'hidden':'' + " btn btn-sm btn-outline btn-secondary mr-3"}
									onClick={handleClearAiSummary}>Clear AI Summary</button>
						</div>
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