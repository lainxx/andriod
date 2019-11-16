from flask import Flask, jsonify

from flask import Flask, request, render_template, redirect, url_for, flash, session
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import and_, or_
import urllib.request,urllib.error
import urllib.request,urllib.error,urllib.parse
from flask import request


app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:199707194535@localhost:3306/app'#配置数据库
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
app.secret_key = '\xc9ixnRb\xe40\xd4\xa5\x7f\x03\xd0y6\x01\x1f\x96\xeao+\x8a\x9f\xe4'
db = SQLAlchemy(app)

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True)
    password = db.Column(db.String(80))
    age= db.Column(db.Integer, primary_key=False)
    name = db.Column(db.String(80))
    gender = db.Column(db.String(80))

    def __repr__(self):
        return '<User %r>' % self.username

def valid_login(username, password):
    user = User.query.filter(and_(User.username == username, User.password == password)).first()
    if user:
        return True
    else:
        return False


# 注册检验（用户名、邮箱验证）
def valid_regist(username):
    user = User.query.filter(or_(User.username == username)).first()
    if user:
        return False
    else:
        return True


# 登录
def login_required(func):
    def wrapper(*args, **kwargs):
        # if g.user:
        if session.get('username'):
            return func(*args, **kwargs)
        else:
            return jsonify({'success': False, 'msg': '请输入用户名'})  #

    return wrapper




# 2.登录
@app.route('/login', methods=['GET', 'POST'])
def login():
    error = None
    if request.method == 'POST':
        if valid_login(request.form['username'], request.form['password']):
            flash("成功登录！")
            session['username'] = request.form.get('username')
            user = db.session.query(User).filter(User.username == request.form.get('username')).one()
            print(user.age)
            return jsonify({'success': True, 'msg': '成功登录','name':user.name,'age':user.age,'gender':user.gender})
        else:
            error = '错误的用户名或密码！'
            return jsonify({'success': False, 'msg': error})



# 3.注销
@app.route('/logout')
def logout():
    session.pop('username', None)
    return jsonify({'success': True, 'msg': '成功注销'})

# 4.注册
@app.route('/regist', methods=['GET', 'POST'])
def regist():
    error = None
    if request.method == 'POST':
        if request.form['password1'] != request.form['password2']:
            error = '两次密码不相同！'
            return jsonify({'success': False, 'msg': error})
        elif valid_regist(request.form['username']):
            user = User(username=request.form['username'], password=request.form['password1'],age=request.form['age'], gender=request.form['gender'],name=request.form['name'])
            db.session.add(user)
            db.session.commit()
            flash("成功注册！")
            return jsonify({'success': True, 'msg': '成功注册'})
        else:
            error = '该用户名已被注册！'
            return jsonify({'success': False, 'msg': error})




'''
人脸搜索
'''

access_token = '24.8c10878e6c0af76a0dd942b50991ec2a.2592000.1574582139.282335-17617499'
@app.route('/face_search', methods=['GET', 'POST'])
def face_search():
    request_url = "https://aip.baidubce.com/rest/2.0/face/v3/search"
    error = None
    params = None
    if request.method == 'POST':
        params = urllib.parse.urlencode({
            'image': request.form['image'],
            'image_type': request.form['image_type'],
            'group_id_list': request.form['group_id_list']
        }).encode(encoding='UTF8')
    request_url = request_url + "?access_token=" + access_token
    req = urllib.request.Request(url=request_url, data=params)
    req.add_header('Content-Type', 'application/json')
    response = urllib.request.urlopen(req)
    content = response.read()
    if content:
        print(content)
    return content


'''
多人脸
'''

access_token = '24.8c10878e6c0af76a0dd942b50991ec2a.2592000.1574582139.282335-17617499'
@app.route('/muface_search', methods=['GET', 'POST'])
def muface_search():
    request_url = "https://aip.baidubce.com/rest/2.0/face/v3/multi-search"
    error = None
    params = None
    if request.method == 'POST':
        params = urllib.parse.urlencode({
            'image': request.form['image'],
            'image_type': request.form['image_type'],
            'group_id_list': request.form['group_id_list'],
            'max_face_num': request.form['max_face_num'],
            'match_threshold': request.form['match_threshold']
        }).encode(encoding='UTF8')
    request_url = request_url + "?access_token=" + access_token
    req = urllib.request.Request(url=request_url, data=params)
    req.add_header('Content-Type', 'application/json')
    response = urllib.request.urlopen(req)
    content = response.read()
    if content:
        print(content)
    return content

'''
人脸注册
'''


@app.route('/face_upload', methods=['GET', 'POST'])
def face_upload():
    error = None
    params = None
    request_url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add"
    if request.method == 'POST':
        params = urllib.parse.urlencode({
            'image': request.form['image'],
            'image_type': request.form['image_type'],
            'group_id': request.form['group_id'],
            'user_id': request.form['user_id'],
            'action_type':request.form['action_type']
        }).encode(encoding='UTF8')
    request_url = request_url + "?access_token=" + access_token
    req = urllib.request.Request(url=request_url, data=params)
    req.add_header('Content-Type', 'application/json')
    response = urllib.request.urlopen(req)
    content = response.read()
    if content:
        print(content)
    return content

'''
人脸检测
'''
@app.route('/face_detect', methods=['GET', 'POST'])
def face_detect():
    error = None
    params = None
    request_url = "https://aip.baidubce.com/rest/2.0/face/v3/detect"
    if request.method == 'POST':
        params = urllib.parse.urlencode({
            'image': request.form['image'],
            'image_type': request.form['image_type'],
            'face_field': 'age,beauty,expression,face_shape,gender,glasses,landmark,landmark150,race,quality,eye_status,emotion,face_type',
            'max_face_num': request.form['max_face_num'],
        }).encode(encoding='UTF8')
    request_url = request_url + "?access_token=" + access_token
    req = urllib.request.Request(url=request_url, data=params)
    req.add_header('Content-Type', 'application/json')
    response = urllib.request.urlopen(req)
    content = response.read()
    if content:
        print(content)
    return content



if __name__ == '__main__':
    app.run()
