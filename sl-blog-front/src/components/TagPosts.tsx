import './PostPage.css';
import { Link, useParams } from 'react-router-dom';
import { useState, useEffect  } from 'react';
import Loading from "../m/components/Loading";
import TagPostsItem from "./TagPostsItem";
import {PostType} from "../types/MyTypes";

function TagPosts() {
	const params = useParams();
	const [articles, setArticles] = useState([]);
	const [tagName, setTagName] = useState('');
	const [tagDescription, setTagDescription] = useState('');
	const [loaded, setLoaded] = useState(false);
	
	useEffect(() => {
		const tagId = params.tagId;
		fetch('/articles/tag/' + tagId)
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setArticles(data.articles);
					setTagName(data.tagName);
					setTagDescription(data.tagDescription);
					setLoaded(true);
				} else {
					alert('Post not loaded...');
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load articles.');
			})
			.finally(() => {
			});
	}, []);
	
	return (
		<>
			<div className="py-4">
				<Loading loaded={loaded}/>
				<div className={loaded ? 'visible' : 'invisible'}>
					<p className="whitespace-pre-wrap mb-3">
						<Link  to="/" className="link link-accent">Blog Top</Link >
					</p>
					<p className="whitespace-pre-wrap mb-3">
						Posts with tag: <span className="badge badge-lg mr-3 shadow-md bg-stone-50">{tagName}</span>
					</p>
					<p className="whitespace-pre-wrap mb-5">
						{tagDescription}
					</p>
					{
						articles.map((a: PostType) => {
							return (<TagPostsItem article={a} key={a.articleId} />);
						})
					}
				</div>
			</div>
		</>
	)

}
export default TagPosts