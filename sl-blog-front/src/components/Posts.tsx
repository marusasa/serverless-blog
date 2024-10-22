import Post from './Post.tsx';
import {PostType} from '../types/MyTypes';
import {useEffect, useState} from 'react'; 

function Posts() {

	const [articles, setArticles] = useState([]);
	
	useEffect(() => {
	      fetch('/articles')
	         .then((response) => response.json())
	         .then((data) => {
					if(data.result == 'success'){
						setArticles(data.articles);
					}else{
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
			{
				articles.map((a:PostType) => {
					return (<Post article={a} key={a.articleId}></Post>);					
				})			
			}	
		</div>
    </>
  )
}

export default Posts