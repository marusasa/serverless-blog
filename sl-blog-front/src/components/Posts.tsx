import PostsItem from './PostsItem';
import { PostType } from '../types/MyTypes';
import { useEffect, useState } from 'react';
import { useOutletContext } from "react-router-dom";
import Loading from "../m/components/Loading";

function Posts() {

	const [articles, setArticles] = useState([]);
	const [parentLoaded, setParentLoaded] = useOutletContext();
	const [loaded, setLoaded] = useState(false);
	const [hasMore, setHasMore] = useState(true);
	const [pagingArray, setPagingArray] = useState([0]);
	const [pageNum, setPageNum]  = useState(1);
	const [pageTotal, setPageTotal] = useState(0);

	const loadData = () => {
		setLoaded(false);
		fetch('/articles/page/' + pagingArray[pageNum-1])
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setLoaded(true);
					setArticles(data.articles);
					setHasMore(data.hasMore);
					if(pagingArray.length == pageNum){
						//add new query val
						pagingArray.push(data.lastQueryVal);
						setPagingArray(pagingArray);
					}
					if(pageNum == 1){
						setPageTotal(data.pageTotal);
					}
					window.scrollTo({top: 0,behavior: 'smooth'});
				} else {
					alert(JSON.stringify(data.messages));
				}
				console.log(data);
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load articles.');
			});
	};
	
	useEffect(() => {
			loadData();
		}, [pageNum]);
	
	const next = () => {
		setPageNum(pageNum+1);
	}
	
	const prev = () => {
		setPageNum(pageNum-1);
	}

	return (
		<>
			<div className="py-4">
				<Loading loaded={(parentLoaded ? loaded : true)} />
				{
					articles.map((a: PostType) => {
						return (<PostsItem article={a} key={a.articleId} />);
					})
				}
			</div>
			<div className={"flex flex-col items-center pb-4 " + (loaded ? '' : 'hidden')}>
				<div>
					<button className={"btn btn-secondary btn-sm mr-3 px-6 " + (pageNum == 1?' btn-disabled ':'')} 
							onClick={prev}>&lt;</button>
					<span>Page: {pageNum}/{pageTotal}</span>
					<button className={'btn btn-secondary btn-sm ml-3 px-6 ' + (hasMore?' ':' btn-disabled ')}
							onClick={next}>&gt;</button>
				</div>	
			</div>
		</>
	)
}

export default Posts