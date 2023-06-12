#!/bin/sh

# IOS TERRITORY
version=$(cat VERSION) || exit

# Save the current branch
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

# Stash any changes
git stash

if [ "$(git branch --list "$version")" ]
then
   echo "Branch name already exists. Deleting..."
   git branch -D "$version" || exit
else
   echo "Branch does not exist!"
fi

echo "Creating $version branch..."
git checkout --orphan "$version" || exit

# Remove the added stuff
git rm --cached -r . || exit

# Build the project
./gradlew assembleMagicsdkReleaseXCFramework || exit

# Copy the XCFramework to the root folder
# shellcheck disable=SC2039
pushd "magicsdk/build/XCFrameworks/release" || exit
zip -r magicsdk.xcframework.zip magicsdk.xcframework
# shellcheck disable=SC2039
popd || exit

# Copy the zip file here
cp magicsdk/build/XCFrameworks/release/magicsdk.xcframework.zip .

git add magicsdk.xcframework.zip
git commit -m "Deployed version $version"
git push origin "$version" -f

# Go back to the previous branch
git checkout "$CURRENT_BRANCH" --force

# Restore the stash
git stash pop
