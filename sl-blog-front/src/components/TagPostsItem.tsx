import './TagPostsItem.css';
import {PostType} from '../types/MyTypes';
import { Link  } from "react-router-dom";
import {useRef } from 'react';
import {convertToAllowedChars} from '../util/LinkUtil';

function TagPostsItem({article}:{article:PostType}) {

	const link = convertToAllowedChars(article.title) + "_" + article.articleId;
	const dialogRef = useRef(null);
	const pubDate = new Date(article.publishedAt);
	const dateText = pubDate.toLocaleDateString();
	
	const handleShowSummary = (e: React.FormEvent) => {
		e.preventDefault();
		dialogRef.current.showModal();
	};
	
	return (
		<>
			<div className="card bg-white w-full mb-8 text-left shadow-lg border-solid border border-slate-200 p-4 md:p-8">
				<div className="card-body p-0">
					<div className="flex flex-wrap">
						<h2 className="card-title"><Link  to={"/post/" + link} className="tag-link mr-5">{article.title}</Link ></h2>
					</div>
					<div className="flex flex-wrap">
						<p className="mt-0.5">
							{dateText} <a className={article.summary == "" || article.summary == null? 
								'invisible':'visible' + " link link-accent"}
								onClick={handleShowSummary}
							>Show AI Summary</a>
						</p>
					</div>
				</div>
			</div>
			<dialog id="my_modal_1" className="modal" ref={dialogRef}>
			  <div className="modal-box">
			    <h3 className="font-bold text-lg">AI Summary:</h3>
			    <p className="py-4">{article.summary}</p>
			    <div className="modal-action">
			      <form method="dialog">
			        <button className="btn">OK</button>
			      </form>
			    </div>
			  </div>
			</dialog>
		</>
	)
}
export default TagPostsItem