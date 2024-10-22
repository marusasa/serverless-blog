export type PostType = {
	title: string;
	body: string;
	status: string;
	articleId: string;
	publishedAt: string;
}


export type ViewCompData = {
	type: string;
	json: string;
	order: number;
	enabled: boolean;	
	pageComponentId: string;
}

export type LinkItem = {
	name: string;
	url: string;
	desc: string;
}