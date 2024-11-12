import './PostPage.css';
import { Link, useParams } from 'react-router-dom';
import { useState, useEffect, useRef } from 'react';
import Markdown from 'react-markdown'
import Loading from "../m/components/Loading";

function PostPage() {
	const params = useParams();
	const [article, setArticle] = useState({});
	const [pubDateYYYYMMDD, setPubDateYYYYMMDD] = useState('');
	const [dateText, setDateText] = useState('');
	const refArticleP = useRef(null);
	const [loaded, setLoaded] = useState(false);
	
	useEffect(() => {
		fetch('/articles/' + params.postId)
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setArticle(data.article);
					setPubDateYYYYMMDD(data.article.publishedAt.substring(0, 10));
					const pubDate = new Date(data.article.publishedAt);
					setDateText(pubDate.toLocaleDateString());
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
	
	const handleArticleClick = (event) => {
		if (event.target.tagName === "A") {
		   // Prevent the default link behavior (navigation)
		   event.preventDefault();
		   window.open(event.target.href,'_blank');		   
		} 
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
							<p className='prose max-w-none' ref={refArticleP}><Markdown className="reactMarkDown">{article.body}</Markdown></p>
						</article>
					</div>
				</div>
			</div>
		</>
	)

}
export default PostPage