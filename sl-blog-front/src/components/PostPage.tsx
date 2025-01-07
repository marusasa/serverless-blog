import './PostPage.css';
import { Link, useParams } from 'react-router-dom';
import { useState, useEffect, useRef } from 'react';
import Markdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import Loading from "../m/components/Loading";
import PostTag from "./PostTag";

function PostPage() {
	const params = useParams();
	const [article, setArticle] = useState({});
	const [pubDateYYYYMMDD, setPubDateYYYYMMDD] = useState('');
	const [dateText, setDateText] = useState('');
	const refArticleP = useRef(null);
	const [loaded, setLoaded] = useState(false);
	const [likeCount, setLikeCount] = useState(0);
	const [processLike, setProcessLike] = useState(false);
	const [likeClicked, setLikeClicked] = useState(false);
	const [tagHtml, setTagHtml] = useState('');
	
	useEffect(() => {
		const postId = params.postId;
		//const index = params.postId.indexOf("#"); 
		//if(index > -1){
		//	postId = postId.substring(0,index);
		//}
		fetch('/articles/' + postId)
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setArticle(data.article);
					setLikeCount(data.article.likes);
					setPubDateYYYYMMDD(data.article.publishedAt.substring(0, 10));
					const pubDate = new Date(data.article.publishedAt);
					setDateText(pubDate.toLocaleDateString());
					const tagItems = data.article.tagIds.map((id,index) => 
						<PostTag tagName={data.article.tagNames[index]} tagId={data.article.tagIds[index]} index={index}/>
					);
					if(tagItems.length > 0){
						tagItems.unshift(<div className="mr-1">Tags:</div>);
					}
					setTagHtml(tagItems);
					setLoaded(true);
				} else {
					alert('Post not loaded...');
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load article.');
			})
			.finally(() => {
				refArticleP.current.addEventListener('click', handleArticleClick);
			});
	}, []);
	
	const handleArticleClick = (event: React.MouseEvent) => {
		if (event.target.tagName === "A") {
			if (event.target.getAttribute("href").toLowerCase().startsWith('http')) {
				// Prevent the default link behavior (navigation)
				event.preventDefault(); 
				window.open(event.target.href, '_blank');
			}
		}
	};
	
	const handleLike = (e: React.FormEvent) => {
		e.preventDefault();
		setProcessLike(true);
		fetch('/articles/' + article.articleId + '/like', {
			method: 'PATCH',
			headers: {
				'Content-type': 'application/json; charset=UTF-8',
			},
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setLikeClicked(true);
					setLikeCount(data.count);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				alert('Failed to like.');
				console.log(err.message);
			})
			.finally(() => {
				setProcessLike(false);
			});
	}
	
	const generateSlug = (input: string) => {
		let str = input.replace(/^\s+|\s+$/g, "");
		str = str.toLowerCase();
		str = str
			.replace(/[^a-z0-9 -]/g, "")
			.replace(/\s+/g, "-")
			.replace(/-+/g, "-");
		return str;
	};
	
	const components = {
		h1: ({ children }) => <h1 id={generateSlug(children)}>{children}</h1>,
	    h2: ({ children }) => <h2 id={generateSlug(children)}>{children}</h2>,
		h3: ({ children }) => <h3 id={generateSlug(children)}>{children}</h3>,
	  };
	
	return (
		<>
			<div className="py-4">
				<div className="card bg-white w-full mb-8 text-left shadow-lg border-solid border border-slate-200 p-4 md:p-8">
					<div className="card-body p-0">
						<Loading loaded={loaded}/>
						<article className={loaded ? 'visible' : 'invisible'}>
							<p>
								<Link  to="/" className="link link-accent">Blog Top</Link >
							</p>
							<h2 className="card-title">{article.title}</h2>
							<p className="font-thin italic"><time dateTime={pubDateYYYYMMDD}>{dateText}</time></p>
							<div className='mb-3 flex'>
								<div className="mr-1">Likes:</div>
								<button className="btn btn-xs btn-ghost mr-3" onClick={handleLike}>
									<svg xmlns="http://www.w3.org/2000/svg" fill={(likeClicked?'oklch(var(--er))':'white')} viewBox="0 0 24 24" strokeWidth={1.0} stroke="currentColor" className="size-6">
									  	<path strokeLinecap="round" strokeLinejoin="round" d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12Z" />
									</svg>
									<span className={'text-base ' + (processLike?'hidden':'')}>{likeCount}</span>
									<span className={(processLike? '':'hidden ') + " loading loading-spinner"}></span>
								</button>
								{tagHtml}
							</div>
							<p className='prose max-w-none' ref={refArticleP}><Markdown className="reactMarkDown" 
									remarkPlugins={[remarkGfm]} components={components}>{article.body}</Markdown></p>
						</article>
					</div>
				</div>
			</div>
		</>
	)

}
export default PostPage