import { LinkItem } from "../types/MyTypes"

function ViewCompListList({title, nameValList}:{title:string, nameValList:LinkItem[]}) {
	
	const list = nameValList.map(item => {
		return (<li className="mb-1"><a href={`${item.url}`} className="link link-accent" 
			target='_blank'>{item.name}</a> - {item.desc}</li>)
	});
	
	return (
		<>
		<div className="shadow-lg bg-teal-100 text-left	p-3 mb-5">
			<p className="text-center mb-2">{title}</p>
			
			<ul>
				{list}
			</ul>
		</div>

		</>
	)
}

export default ViewCompListList