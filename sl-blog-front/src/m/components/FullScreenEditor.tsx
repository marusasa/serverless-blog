import './FullScreenEditor.css'
import {  forwardRef,useState,useRef,useEffect } from 'react'
import Markdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import {SubmitButton} from "../components/FormComp";
import ImageManager from './ImageManager';
import AIMode from './AIMode';
import FullScreenEditorHelp from './FullScreenEditorHelp';

const FullScreenEditor  = forwardRef(function(props, ref) {
	const [showImgMgr, setShowImgMgr] = useState(false);
	const refArticleOuter = useRef(null);
	const refTextrea = useRef(null);
	const [modePreview, setModePreview] = useState(true);
	
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
	useEffect(() => {
		refArticleOuter.current.addEventListener('click', handleArticleClick);
	}, []);

	useEffect(() => {
		const scrollable1 = refTextrea.current;		
		const scrollable2 = refArticleOuter.current;
		syncScrollbars(scrollable1, scrollable2);
	}, []);	
	
	
	const handleArticleClick = (event) => {
		if (event.target.tagName === "A") {
			event.preventDefault(); 
			window.open(event.target.href, '_blank');
		}
	};
	
	const syncScrollbars = (scrollable1, scrollable2) => {
	  scrollable1.addEventListener('scroll', function() {
	    const scrollPercentage = (scrollable1.scrollTop / (scrollable1.scrollHeight - scrollable1.clientHeight)) * 100;
	    scrollable2.scrollTop = (scrollPercentage / 100) * (scrollable2.scrollHeight - scrollable2.clientHeight);
	  });
	}

	const modeleToPreview = (event) => {
		event.preventDefault();
		setModePreview(true);
	};
	const modeleToAI = (event) => {
		event.preventDefault();
		setModePreview(false);
	};
	
	return (
		<>
			<dialog id="full_screen_editor" className="modal" ref={ref}>
				<div className="modal-box w-full max-w-full h-lvh max-h-lvh rounded-none">
					<div className="flex flex-row h-full">
						<div className="basis-1/2 h-full flex-none">
							<div className="mb-3 flex">
								<SubmitButton text="Save" inProcess={props.inSave} callback={props.handleSave} classes="btn-sm btn-primary"/>
								<button className="btn btn-sm mr-3" onClick={close}>Close</button>
								<button className="btn btn-sm mr-3"  onClick={openImgMgr}>Image Manager</button>
								<button className="btn btn-sm  mr-3" onClick={()=>document.getElementById('dialog_fullscreen_help').showModal()}>?</button>
								<span className="text-red-500 mt-1">{props.saveMsg}</span>
							</div>
							<textarea className="textarea textarea-bordered w-full" value={props.body}
								rows={25} ref={refTextrea}
								onChange={(e) => { props.setBody(e.target.value); props.setPostChanged(true); }}></textarea>
							<FullScreenEditorHelp idName='dialog_fullscreen_help'/>
						</div>
						<div className="basis-1/2 h-full ">
							<div className="mb-3 flex flex-none">
								<div role="tablist" className="ml-4 tabs tabs-boxed">
									<a role="tab" className={"tab " + (modePreview ? 'tab-active' : '')} onClick={modeleToPreview}>Preview Mode</a>
									<a role="tab" className={"tab " + (modePreview ? '' : 'tab-active')} onClick={modeleToAI}>AI Mode</a>
								</div>
							</div>
							<AIMode className={modePreview?'hidden':''} article={props.body} />
							<div className={"h-full overflow-auto " + (modePreview?'':'hidden')} ref={refArticleOuter}>
								<Markdown className="prose max-w-none mx-6 reactMarkDown h-full"																
									remarkPlugins={[remarkGfm]}>{props.body}</Markdown>
							</div>
						</div>
					</div>
				</div>
			</dialog>
			<ImageManager isOpen={showImgMgr} onClose={closeImgMgr} articleId={props.articleId}/>
		</>
	)
})

export default FullScreenEditor