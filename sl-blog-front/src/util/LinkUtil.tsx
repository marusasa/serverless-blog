export function convertToAllowedChars(str: string) {
	str = str.replaceAll(' ', '-');
	const allowedCharacters: string = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-"
	// Create a regular expression that matches any character NOT in the allowedChars string
	const regex = new RegExp(`[^${allowedCharacters}]`, 'g');

	// Replace all non-allowed characters with an empty string
	return str.replace(regex, '');
}