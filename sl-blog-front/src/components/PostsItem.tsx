import {PostType} from '../types/MyTypes';
import { Link  } from "react-router-dom";
import {useRef } from 'react';
import {convertToAllowedChars} from '../util/LinkUtil';

function PostsItem({article}:{article:PostType}) {

	//article.publishedAt will be in UTC timezone with
	//format: '2024-10-25T04:36:17.743Z'
	const pubDateYYYYMMDD = article.publishedAt.substring(0,10);	//result yyyy-mm-dd format.
	const pubDate = new Date(article.publishedAt);
	const dateText = pubDate.toLocaleDateString();
	const dialogRef = useRef(null);
	
	const handleShowSummary = (e: React.FormEvent) => {
		e.preventDefault();
		dialogRef.current.showModal();
	};
	
  	const link = convertToAllowedChars(article.title) + "_" + article.articleId;
	
	return (
		<>
			<div className="card bg-white w-full mb-8 text-left shadow-lg border-solid border border-slate-200 p-4 md:p-8">
				<div className="card-body p-0">
					<h2 className="card-title">{article.title}</h2>
					<p className="font-thin italic"><time dateTime={pubDateYYYYMMDD}>{dateText}</time></p>
					<p className='whitespace-pre-wrap line-clamp-4 mb-3'>{article.body}</p>
					<p>
						<Link  to={"/post/" + link} className="link link-accent mr-5">Read More...</Link >
						<a className={article.summary == "" || article.summary == null? 
							'invisible':'visible' + " link link-accent"}
							onClick={handleShowSummary}
						>Show AI Summary</a>
					</p>
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
export default PostsItem


