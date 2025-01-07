import { useState,useEffect,useRef } from 'react';
import { useLocation, useNavigate } from "react-router-dom";
import Loading from "../components/Loading";
import {SubmitButton, FormTitle} from "../components/FormComp";
import FullScreenEditor from '../components/FullScreenEditor';
import Select from 'react-select'

function PostEdit() {
	const navigate = useNavigate();
	const {state} = useLocation();
	const {articleId} = state; // Read values passed on state
	
	const [title, setTitle] = useState('');
	const [body, setBody] = useState('');
	const [status, setStatus] = useState('draft'); // Set initial value
	const [aiSummary, setAiSummary] = useState('');
	const [tagIds, setTagIds] = useState([]);
	const [selectedTagIds, setSelectedTagIds] = useState({});
	const [loaded, setLoaded] = useState(false);
	const [loadedTags, setLoadedTags] = useState(false);
	const [inSave,setInSave] = useState(false);
	const [inPubSave,setInPubSave] = useState(false);
	const [inDelete,setInDelete] = useState(false);
	const [inGenAi,setInGenAi] = useState(false);
	const [postChanged, setPostChanged] = useState(false);
	const dialogRef = useRef(null);
	const [saveMsg,setSaveMsg] = useState('');
	const [options,setOptions] = useState([]);
	
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
					setTagIds(data.article.tagIds);
					const defaultListIds = [];
					for (let i = 0; i < data.article.tagIds.length; i++) {
						const o = {};
						o.label = data.article.tagNames[i];
						o.value = data.article.tagIds[i];
						defaultListIds.push(o);
					}
					setSelectedTagIds(defaultListIds);
					setLoaded(true);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load article.');
			});
		fetch('/mng/tags')
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					const optionList = [];
					data.tags.forEach((tag) => {
						const o = {};
						o.label = tag.name;
						o.value = tag.tagId;
						optionList.push(o);
					});
					setOptions(optionList);
					setLoadedTags(true);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load tags.');
			});
	}, []);
		
		
	const updatePost = async (title: string, body: string, status: string) => {
		await fetch('/mng/articles/', {
			method: 'PATCH',
			body: JSON.stringify({
				title: title, body: body, status: status, articleId: articleId, summary: aiSummary, tagIds: tagIds
			}),
			headers: {
				'Content-type': 'application/json; charset=UTF-8',
			},
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setSaveMsg('Data Saved.');
					setTimeout(() => {
						setSaveMsg('');
					},2000);
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
	const handleOpenFullScreenEditor = (e: React.FormEvent) => {
		e.preventDefault();
		dialogRef.current.showModal();			
	};
	const handleTagChange = (selected: HTMLSelectElement[]) => {
		const tgIds:string[] = [];
		selected.forEach(o => {
			tgIds.push(o.value);
		});
		setTagIds(tgIds);
		setSelectedTagIds(selected);
	};
	return (
		<>			
			<FormTitle text={"Edit Post : " + (status==='draft'?'Draft':'Published')}/>
			<Loading loaded={loaded && loadedTags}/>
			<div className={(loaded && loadedTags) ? 'visible' : 'invisible'}>
				<form>
					<div className='flex'>
						<SubmitButton text="Save" inProcess={inSave} callback={handleSave} classes="btn-sm btn-primary"/>
						<SubmitButton text={status === 'draft'?'Publish':'Unpublish'} inProcess={inPubSave} callback={handlePupUnPub} classes="btn-sm btn-secondary"/>
						<button className="btn btn-sm mr-3" onClick={handleCancel}>Back</button>	
						<SubmitButton text="Delete" inProcess={inDelete} callback={handleDelete} classes="btn-sm btn-accent"/>
						<span className="text-red-500 mt-1">{saveMsg}</span>
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
					<div className="mb-4">
						<div className="label">
							<span className="label-text">Tags:</span>
						</div>
						<div>
							<Select options={options} isMulti value={selectedTagIds} onChange={handleTagChange}/>
						</div>
					</div>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Article: 
								<button className="ml-3 btn btn-xs" onClick={handleOpenFullScreenEditor}>Full-Screen Editor</button></span>
						</div>
						<textarea className="textarea textarea-bordered w-full max-w-5xl" value={body}
								onChange={(e) => {setBody(e.target.value);setPostChanged(true);}} rows={20}></textarea>
					</label>
					
				</form>
			</div>
			<FullScreenEditor ref={dialogRef} body={body} setBody={setBody} setPostChanged={setPostChanged} 
					handleSave={handleSave} inSave={inSave} saveMsg={saveMsg} articleId={articleId}/>
		</>
	)
}

export default PostEdit