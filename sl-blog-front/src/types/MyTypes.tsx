export type PostType = {
	title: string;
	body: string;
	status: string;
	articleId: string;
	publishedAt: string;
	createdAt: string;
	summary: string;
}


export type ViewCompData = {
	type: string;
	json: string;
	order: number;
	enabled: boolean;	
	pageComponentId: string;
}

export type ViewTagData = {
	name: string;
	json: string;
	description: string;
	tagId: string;
}

export type LinkItem = {
	name: string;
	url: string;
	desc: string;
}

export type TagItem = {
	name: string;
	tagId: string;
}

export type ImageItem = {
	fileName: string;
	url: string;	
}