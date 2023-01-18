import * as fs from "fs";
import * as path from "path";
import {markdownTable} from 'markdown-table'


const folderDenylist = [ '.github', '.git' ];

const runtimes = fs.readdirSync(path.join('.', '../../../'), { withFileTypes: true })
    .filter(dirent => dirent.isDirectory())
    .map(dirent => dirent.name)
    .filter((folder) => !folderDenylist.includes(folder));

const examples = [];

for(const runtime of runtimes) {
    const folders = fs.readdirSync(path.join('.', `../../../${runtime}`), { withFileTypes: true })
        .filter(dirent => dirent.isDirectory())
        .map(dirent => dirent.name);
    examples.push(...folders);
}

const uniqueExamples = [...new Set(examples)];

const rows = uniqueExamples.map((example) => {
    const languagesSupport = runtimes.map((runtime) => {
        return fs.existsSync(path.join('.', `../../../${runtime}/${example}`)) ? '✅' : '';
    })

    return [example, ...languagesSupport];
});

const table = markdownTable([
    ['Example', ...runtimes],
    ...rows.sort((a, b) => {
        const aCount = a.filter((column) => column === '✅');
        const bCount = b.filter((column) => column === '✅');

        return aCount > bCount ? -1 : 1;
    })
  ]);


const readmePath = path.join(__dirname, "../../../README.md");
const readme = fs.readFileSync(readmePath).toString();
let newReadme = '';

if(readme.includes('<!-- EXAMPLES-LIST:START -->') && readme.includes('<!-- EXAMPLES-LIST:END -->')) {
    newReadme += readme.split('<!-- EXAMPLES-LIST:START -->')[0];
    newReadme += '<!-- EXAMPLES-LIST:START -->\n';

    newReadme += table;

    newReadme += '\n<!-- EXAMPLES-LIST:END -->';
    newReadme += readme.split('<!-- EXAMPLES-LIST:END -->')[1];
}

fs.writeFileSync(readmePath, newReadme);