import { useLoaderData, Link } from 'react-router-dom';
import { useState, useEffect } from 'react';

function PostPage() {
	const articleRes = useLoaderData() as object;	
	const [article, setArticle] = useState({});
	const [pubDateYYYYMMDD, setPubDateYYYYMMDD] = useState('');
	const [dateText, setDateText] = useState('');
	
	useEffect(() => {
		try{
			if (articleRes.result == 'success') {
				setArticle(articleRes.article);
				setPubDateYYYYMMDD(articleRes.article.publishedAt.substring(0, 10));
				const pubDate = new Date(articleRes.article.publishedAt);
				setDateText(pubDate.toLocaleDateString());
			} else {
				alert('Post not loaded.');
			}
		}catch(error){
			console.log(error);
			alert('Error loading data.');
		}
	});
	
	return (
		<>
			<div className="py-4">
				<div className="card bg-white w-full mb-8 text-left shadow-lg border-solid border border-slate-200 p-4 md:p-8">
					<div className="card-body p-0">
						<article>
							<p>
								<Link  to="/" className="link link-accent">Blog Top</Link >
							</p>
							<h2 className="card-title">{article.title}</h2>
							<p className="font-thin italic"><time dateTime={pubDateYYYYMMDD}>{dateText}</time></p>
							<p className='whitespace-pre-wrap'>{article.body}</p>
						</article>
					</div>
				</div>
			</div>
		</>
	)

}
export default PostPage

export async function loader({ params }:{params:object}) {
	const articleId: string = params.postId;
	return fetch('/articles/' + articleId);
}