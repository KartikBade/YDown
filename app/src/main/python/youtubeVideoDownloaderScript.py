from pytube import YouTube

ytube = YouTube('https://www.youtube.com/watch?v=ePXZguxYewI')
videos = ytube.streams.all()

def yt():
    vid = list((videos))
    return vid

def download(position):
    videos[position].download("/storage/emulated/0/Download/YDownTestFolder")