let token = "";
let currentRole = "";
let currentUsername = "";

const loginSection = document.getElementById("login-section");
const memberSection = document.getElementById("member-section");
const adminSection = document.getElementById("admin-section");

const loginButton = document.getElementById("login-button");
const logoutButton = document.getElementById("logout-button");
const addMemberButton = document.getElementById("add-member-button");

const loginMessage = document.getElementById("login-message");
const memberTableBody = document.getElementById("member-table-body");

loginButton.addEventListener("click", login);
logoutButton.addEventListener("click", logout);
addMemberButton.addEventListener("click", addMember);


// 登入
async function login() {

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    loginMessage.textContent = "";

    try {

        const response = await fetch("/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        if (!response.ok) {
            loginMessage.textContent = "帳號或密碼錯誤";
            return;
        }

        token = await response.text();

        currentUsername = username;
        currentRole = getRoleFromToken(token);

        document.getElementById("current-user").textContent =
            currentUsername;

        document.getElementById("current-role").textContent =
            currentRole;

        loginSection.style.display = "none";
        memberSection.style.display = "block";

        if (currentRole === "ADMIN") {
            adminSection.style.display = "block";
        } else {
            adminSection.style.display = "none";
        }

        await loadMembers();

    } catch (error) {
        console.error(error);
        loginMessage.textContent = "登入發生錯誤";
    }
}


// 讀取 JWT 裡面的 role
function getRoleFromToken(jwtToken) {

    const payload = jwtToken.split(".")[1];

    const decodedPayload = JSON.parse(
        atob(
            payload
                .replace(/-/g, "+")
                .replace(/_/g, "/")
        )
    );

    return decodedPayload.role;
}


// 查詢會員
async function loadMembers() {

    try {

        const response = await fetch("/api/members", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            alert("取得會員資料失敗");
            return;
        }

        const members = await response.json();

        renderMembers(members);

    } catch (error) {
        console.error(error);
    }
}


// 顯示會員資料
function renderMembers(members) {

    memberTableBody.innerHTML = "";

    members.forEach(member => {

        const row = document.createElement("tr");

        let actionButtons = "";

        if (currentRole === "ADMIN") {

            actionButtons = `
                <button onclick="deleteMember(${member.id})">
                    刪除
                </button>
            `;
        }

        row.innerHTML = `
            <td>${member.id}</td>
            <td>${member.name}</td>
            <td>${member.email}</td>
            <td>${member.age}</td>
            <td>${member.status}</td>
            <td>${actionButtons}</td>
        `;

        memberTableBody.appendChild(row);
    });
}


// 新增會員
async function addMember() {

    if (currentRole !== "ADMIN") {
        alert("你沒有新增會員的權限");
        return;
    }

    const name =
        document.getElementById("member-name").value;

    const email =
        document.getElementById("member-email").value;

    const age =
        document.getElementById("member-age").value;

    const status =
        document.getElementById("member-status").value;

    try {

        const response = await fetch("/api/members", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify({
                name: name,
                email: email,
                age: Number(age),
                status: status
            })
        });

        if (!response.ok) {
            alert("新增會員失敗");
            return;
        }

        clearMemberForm();

        await loadMembers();

    } catch (error) {
        console.error(error);
    }
}


// 刪除會員
async function deleteMember(id) {

    if (currentRole !== "ADMIN") {
        alert("你沒有刪除會員的權限");
        return;
    }

    const confirmed =
        confirm("確定要刪除這位會員嗎？");

    if (!confirmed) {
        return;
    }

    try {

        const response = await fetch(
            "/api/members/" + id,
            {
                method: "DELETE",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        if (!response.ok) {
            alert("刪除會員失敗");
            return;
        }

        await loadMembers();

    } catch (error) {
        console.error(error);
    }
}


// 清空新增會員表單
function clearMemberForm() {

    document.getElementById("member-name").value = "";
    document.getElementById("member-email").value = "";
    document.getElementById("member-age").value = "";
    document.getElementById("member-status").value = "ACTIVE";
}


// 登出
function logout() {

    token = "";
    currentRole = "";
    currentUsername = "";

    memberTableBody.innerHTML = "";

    memberSection.style.display = "none";
    adminSection.style.display = "none";

    loginSection.style.display = "block";

    document.getElementById("password").value = "";
}