import {PostType} from '../types/MyTypes';

function Post({article}:{article:PostType}) {

	const pubDateYYYYMMDD = article.publishedAt.substring(0,10);
	const pubDate = new Date(pubDateYYYYMMDD);
	const dateText = pubDate.toLocaleDateString(undefined, {timeZone: 'UTC'}); 
	
	return (
		<>
			<div className="card bg-white w-full mb-8 text-left shadow-lg border-solid border border-slate-200 p-4 md:p-8">
				<div className="card-body p-0">
					<article>
						<h2 className="card-title">{article.title}</h2>
						<p className="font-thin italic"><time dateTime={pubDateYYYYMMDD}>{dateText}</time></p>
						<p className='whitespace-pre-wrap'>{article.body}</p>
					</article>
				</div>
			</div>
		</>
	)
}

export default Post