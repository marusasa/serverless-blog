import PostsItem from './PostsItem';
import {PostType} from '../types/MyTypes';
import {useEffect, useState} from 'react'; 
import { useOutletContext } from "react-router-dom";
import Loading from "../m/components/Loading";

function Posts() {

	const [articles, setArticles] = useState([]);
	const [parentLoaded, setParentLoaded] = useOutletContext();
	const [loaded, setLoaded] = useState(false);
	
	useEffect(() => {
		setLoaded(false);
		fetch('/articles')
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setLoaded(true);
					setArticles(data.articles);
				} else {
					alert(JSON.stringify(data.messages));
				}
				console.log(data);
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load articles.');
			});
	}, []);
	
  return (
    <>		
		<div className="py-4">
			<Loading loaded={(parentLoaded? loaded : true )}/>	
			{
				articles.map((a:PostType) => {
					return (<PostsItem article={a} key={a.articleId}/>);					
				})			
			}	
		</div>
    </>
  )
}

export default Posts