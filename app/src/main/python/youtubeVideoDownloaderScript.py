from pytube import YouTube

def yt(link):
    ytube = YouTube(link)
    videos = ytube.streams.all()
    vid = list((videos))
    return vid

def download(link, position, destination):
    ytube = YouTube(link)
    videos = ytube.streams.all()
    videos[position].download(destination)