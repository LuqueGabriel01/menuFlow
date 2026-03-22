#!/bin/bash
echo "Installing Git Hooks..."

cat <<EOF > .git/hooks/commit-msg
#!/bin/bash

commit_msg=\$(cat \$1)
pattern="^(feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert)(\(.+\))?: .+"

if [[ ! \$commit_msg =~ \$pattern ]]; then
    echo "--------------------------------------------------------"
    echo "ERROR: Invalid commit message format."
    echo "Allowed types: feat, fix, refactor, test, chore, docs"
    echo "Example: feat: add jwt authentication"
    echo "--------------------------------------------------------"
    exit 1
fi
EOF

chmod +x .git/hooks/commit-msg
echo "Hooks installed successfully!"
