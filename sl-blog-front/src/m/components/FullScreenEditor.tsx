import './FullScreenEditor.css'
import {  forwardRef,useState } from 'react'
import Markdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import {SubmitButton} from "../components/FormComp";
import ImageManager from './ImageManager';

const FullScreenEditor  = forwardRef(function(props, ref) {
	const [showImgMgr, setShowImgMgr] = useState(false);
	
	const close = (e: React.FormEvent) => {
		e.preventDefault();
		ref.current.close();			
	};
	const openImgMgr = (e: React.FormEvent) => {
		e.preventDefault();
		setShowImgMgr(true);			
	}; 
	const closeImgMgr =	(e: React.FormEvent) => {
		if(e != undefined){
			e.preventDefault();
		}		
		setShowImgMgr(false);			
	};  
	
	
	return (
		<>
			<dialog id="full_screen_editor" className="modal" ref={ref}>
				<div className="modal-box w-full max-w-full h-lvh max-h-lvh rounded-none">
					<div className="flex flex-row h-full">
						<div className="basis-1/2 h-full">
							<div className="mb-3 flex">
								<SubmitButton text="Save" inProcess={props.inSave} callback={props.handleSave} classes="btn-sm btn-primary"/>
								<button className="btn btn-sm mr-3" onClick={close}>Close</button>
								<button className="btn btn-sm mr-3"  onClick={openImgMgr}>Image Manager</button>
								<span className="text-red-500 mt-1">{props.saveMsg}</span>
							</div>
							<textarea className="textarea textarea-bordered w-full" value={props.body}
								rows={25}
								onChange={(e) => { props.setBody(e.target.value); props.setPostChanged(true); }}></textarea>
						</div>
						<div className="basis-1/2 h-full">
							<Markdown className="prose max-w-none mx-6 reactMarkDown h-full overflow-auto"
								remarkPlugins={[remarkGfm]}>{props.body}</Markdown>
						</div>
					</div>
				</div>
			</dialog>
			<ImageManager isOpen={showImgMgr} onClose={closeImgMgr} articleId={props.articleId}/>
		</>
	)
})

export default FullScreenEditor